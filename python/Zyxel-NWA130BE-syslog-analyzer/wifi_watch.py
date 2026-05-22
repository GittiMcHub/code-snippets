#!/usr/bin/env python3
"""
Zyxel NWA WiFi Analyzer
Wraps tcpdump. Ctrl+C prints summary report.
Usage: sudo python3 wifi_watch.py
"""

import subprocess
import re
import sys
import signal
from collections import defaultdict
from datetime import datetime

AP_IP = "192.168.0.130"
IFACE = "any"

class DeviceStats:
    def __init__(self, mac):
        self.mac = mac
        self.connects = []      # (ts, band, channel, signal)
        self.disconnects = []   # (ts, action, band, channel, signal, reason)
        self.kickouts = 0
        self.deauths = 0
        self.all_signals = []
        self.bands = defaultdict(int)

RE_CEF = re.compile(r'NWA130BE CEF:0\|Zyxel.*?msg=(.+)')
RE_TS = re.compile(r'(\w{3}\s+\d+\s+\d+:\d+:\d+) NWA130BE')
RE_CONNECTED = re.compile(
    r'Station: (\S+) connected on Channel: (\d+), SSID: \S+, (\S+), Signal: (-?\d+)dBm'
)
RE_EVENT = re.compile(
    r'Station: (\S+) (left|disconnected .+?|blocked .+?) on Channel: (\d+), SSID: \S+, (\S+), Signal: (-?\d+)dBm(?:.*?reason (\d+))?'
)
RE_KICKOUT = re.compile(r'Signal Threshold Kickout.*?STA (\S+),')
RE_DEAUTH = re.compile(r'Station: (\S+) deauthenticated')

REASON_CODES = {
    "1": "unspecified",
    "2": "auth expired",
    "3": "leaving BSS",
    "4": "inactivity",
    "5": "AP overloaded",
    "6": "class2 non-auth",
    "7": "class3 non-assoc",
    "8": "STA left (roam/sleep)",
    "9": "assoc without auth",
    "17": "MIC failure",
    "23": "802.1X auth failed",
    "34": "BSS transition/band steer",
    "36": "requested by STA",
}

devices = {}
proc = None

def get_dev(mac):
    mac = mac.lower()
    if mac not in devices:
        devices[mac] = DeviceStats(mac)
    return devices[mac]

def process_msg(ts, msg):
    m = RE_KICKOUT.search(msg)
    if m:
        get_dev(m.group(1)).kickouts += 1
        print(f"  [{ts}] {m.group(1)}  KICKOUT")
        return

    m = RE_DEAUTH.search(msg)
    if m:
        get_dev(m.group(1)).deauths += 1
        print(f"  [{ts}] {m.group(1)}  DEAUTH")
        return

    m = RE_CONNECTED.search(msg)
    if m:
        mac, channel, band, signal = m.group(1), m.group(2), m.group(3), int(m.group(4))
        dev = get_dev(mac)
        dev.connects.append((ts, band, channel, signal))
        dev.all_signals.append(signal)
        dev.bands[band] += 1
        print(f"  [{ts}] {mac}  CONNECT    {band} ch{channel} {signal}dBm")
        return

    m = RE_EVENT.search(msg)
    if m:
        mac = m.group(1)
        action = m.group(2).strip()
        channel, band = m.group(3), m.group(4)
        signal = int(m.group(5))
        reason = m.group(6) or "?"
        dev = get_dev(mac)
        dev.disconnects.append((ts, action, band, channel, signal, reason))
        dev.all_signals.append(signal)
        reason_desc = REASON_CODES.get(reason, "unknown")
        print(f"  [{ts}] {mac}  {action.upper():<20} {band} ch{channel} {signal}dBm  reason={reason} ({reason_desc})")

def parse_line(line):
    m = RE_CEF.search(line)
    if not m:
        return
    msg = m.group(1).strip()
    ts_m = RE_TS.search(line)
    ts = ts_m.group(1) if ts_m else "?"
    process_msg(ts, msg)

def print_report():
    if not devices:
        print("\nNo events captured.")
        return

    print("\n" + "═" * 68)
    print("  ZYXEL WIFI SESSION REPORT")
    print(f"  {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("═" * 68)

    for mac, dev in sorted(devices.items()):
        roaming    = sum(1 for d in dev.disconnects if "Intra Roaming"   in d[1])
        sta_to     = sum(1 for d in dev.disconnects if d[5] == "34")
        lower_sig  = sum(1 for d in dev.disconnects if "Lower STA Signal" in d[1])
        rate_mis   = sum(1 for d in dev.disconnects if "Rate Mismatch"   in d[1])
        reconnect  = sum(1 for d in dev.disconnects if "STA reconnect"   in d[1])
        auth_fail  = sum(1 for d in dev.disconnects if "Auth Timeout"    in d[1]) + dev.deauths

        avg_sig = sum(dev.all_signals) / len(dev.all_signals) if dev.all_signals else 0
        min_sig = min(dev.all_signals) if dev.all_signals else 0
        band_str = "  ".join(f"{b}:{c}x" for b, c in sorted(dev.bands.items()))

        print(f"\n  ┌─ {mac}")
        print(f"  │  connects:    {len(dev.connects)}    disconnects: {len(dev.disconnects)}")
        print(f"  │  roaming:     {roaming}x   sta-timeout: {sta_to}x   kickouts: {dev.kickouts}x")
        if lower_sig or rate_mis or auth_fail or reconnect:
            print(f"  │  lower-sig:   {lower_sig}x   rate-mismatch: {rate_mis}x   auth-fail: {auth_fail}x   reconnects: {reconnect}x")
        print(f"  │  signal:      avg {avg_sig:.0f} dBm   worst {min_sig} dBm")
        print(f"  │  bands:       {band_str or 'none recorded'}")

        issues = []
        if roaming > 5:
            issues.append(f"roams {roaming}x → marginal coverage, band steering or AP placement")
        if sta_to > 0:
            issues.append(f"STA timeout {sta_to}x → hard signal loss, device leaves range")
        if dev.kickouts > 2:
            issues.append(f"kicked {dev.kickouts}x below -70dBm on 2.4GHz → move device closer or raise 2.4GHz TX power")
        if lower_sig > 3:
            issues.append(f"kicked {lower_sig}x for low signal → consistently at signal floor")
        if rate_mis > 0:
            issues.append(f"blocked from 6GHz {rate_mis}x → device not WiFi 6E capable")
        if avg_sig < -78:
            issues.append(f"poor avg signal {avg_sig:.0f} dBm → too far from AP or obstructed")
        if auth_fail > 0:
            issues.append(f"auth failures {auth_fail}x → driver issue or credential problem")

        if issues:
            for i in issues:
                print(f"  │  ⚠  {i}")
        else:
            print(f"  │  ✓  connection healthy")
        print(f"  └{'─' * 55}")

    # Rankings
    print(f"\n  RANKINGS")
    print(f"  {'─' * 45}")

    by_roaming = sorted(
        devices.items(),
        key=lambda x: sum(1 for d in x[1].disconnects if "Intra Roaming" in d[1]),
        reverse=True
    )
    roam_str = "  |  ".join(
        f"{m}: {sum(1 for d in dev.disconnects if 'Intra Roaming' in d[1])}x"
        for m, dev in by_roaming
        if sum(1 for d in dev.disconnects if "Intra Roaming" in d[1]) > 0
    )
    print(f"  Roaming:  {roam_str or 'none'}")

    by_signal = sorted(
        [(m, d) for m, d in devices.items() if d.all_signals],
        key=lambda x: sum(x[1].all_signals) / len(x[1].all_signals)
    )
    sig_str = "  |  ".join(
        f"{m}: {sum(d.all_signals)/len(d.all_signals):.0f}dBm avg"
        for m, d in by_signal
    )
    print(f"  Signal:   {sig_str or 'none'}")

    def issue_score(dev):
        return (
            sum(1 for d in dev.disconnects if d[5] == "34") * 3 +
            dev.kickouts * 2 +
            sum(1 for d in dev.disconnects if "Rate Mismatch" in d[1]) * 2 +
            sum(1 for d in dev.disconnects if "Lower STA Signal" in d[1])
        )

    worst = max(devices.items(), key=lambda x: issue_score(x[1]), default=None)
    if worst and issue_score(worst[1]) > 0:
        print(f"  Needs attention: {worst[0]} (score {issue_score(worst[1])})")

    print("═" * 68 + "\n")

def cleanup(sig, frame):
    total = sum(len(d.connects) + len(d.disconnects) for d in devices.values())
    print(f"\nStopped. {total} events from {len(devices)} devices.")
    if proc:
        proc.terminate()
    print_report()
    sys.exit(0)

signal.signal(signal.SIGINT, cleanup)

def main():
    global proc
    cmd = [
        "tcpdump", "-i", IFACE,
        f"udp port 514 and src host {AP_IP}",
        "-l", "-A", "-n"
    ]
    print(f"WiFi monitor — AP: {AP_IP} | Ctrl+C to stop and report")
    print(f"Started: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
    try:
        proc = subprocess.Popen(
            cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
            text=True, bufsize=1
        )
    except FileNotFoundError:
        print("tcpdump not found: sudo apt install tcpdump")
        sys.exit(1)

    for line in proc.stdout:
        parse_line(line.rstrip())

    # tcpdump exited on its own (error or EOF)
    err = proc.stderr.read().strip()
    if err:
        print(f"tcpdump error: {err}", file=sys.stderr)
    print_report()

if __name__ == "__main__":
    main()
