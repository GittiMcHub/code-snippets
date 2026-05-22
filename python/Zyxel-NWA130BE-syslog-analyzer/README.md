# wifi_watch.py

On-demand WiFi session analyzer for Zyxel NWA access points.
Run it, move around, Ctrl+C — get a per-device report.

## What it does

Wraps `tcpdump` to capture CEF syslog events from the AP and tracks:

- Roaming events (band switches: 2.4 / 5 / 6 GHz)
- STA timeouts (hard disconnects, reason 34)
- Signal threshold kickouts (AP-initiated for weak signal)
- Lower STA Signal disconnects
- 6 GHz association failures (Rate Mismatch = device not WiFi 6E capable)
- Auth failures / deauth events
- Per-device signal average and worst value
- Band usage distribution

On Ctrl+C prints a structured report with per-device stats, issue flags, and rankings.

## Requirements

- Python 3.6+
- `tcpdump` (`sudo apt install tcpdump`)
- AP configured to send CEF/Syslog to the host running the script (UDP 514)

No extra Python dependencies. No always-running daemon.

## AP syslog format

Tested with Zyxel NWA series (NWA130BE, firmware V7.30). Expects CEF format:

```
CEF:0|Zyxel|NWA130BE|...|msg=Station: <mac> connected on Channel: 40, SSID: ..., 5GHz, Signal: -58dBm, ...
CEF:0|Zyxel|NWA130BE|...|msg=Station: <mac> disconnected by Intra Roaming on Channel: 1, ..., reason 5, ...
CEF:0|Zyxel|NWA130BE|...|msg=Signal Threshold Kickout Timer Timeout: STA <mac>, RSSI -79 dBm, ...
```

Enable these AP log categories: `sta roaming`, `Wlan Station Info`, `WLAN Band Select`, `Wireless Health`, `Wireless LAN`.

## Configuration

Edit the two constants at the top of the script:

```python
AP_IP = "192.168.x.x"   # IP of your Zyxel AP
IFACE = "any"            # network interface, or e.g. "eth0"
```

## Device name map (optional)

Pass a `key=value` file to replace MAC addresses with friendly names:

```
# devices.txt
aa:bb:cc:dd:ee:ff=living-room-tv
11:22:33:44:55:66=macbook-pro
de:ad:be:ef:00:01=phone-alice
```

- One entry per line. `#` = comment. MACs case-insensitive.
- Live output shows the name. Report header shows `name [mac]`.

## Usage

```bash
sudo python3 wifi_watch.py
sudo python3 wifi_watch.py --device-map devices.txt
```

Uses `tcpdump` passively — does not bind port 514. Safe to run alongside other syslog listeners (e.g. Wazuh).

Requires `sudo` for `tcpdump` packet capture.

## Sample output

```
════════════════════════════════════════════════════════════════════
  ZYXEL WIFI SESSION REPORT
  2026-05-22 14:30:00
════════════════════════════════════════════════════════════════════

  ┌─ macbook-pro [aa:bb:cc:dd:ee:ff]
  │  connects:    12    disconnects: 14
  │  roaming:     11x   sta-timeout: 0x   kickouts: 0x
  │  signal:      avg -63 dBm   worst -73 dBm
  │  bands:       2.4GHz:6x  5GHz:6x
  │  ⚠  roams 11x → marginal coverage, band steering or AP placement
  └───────────────────────────────────────────────────────

  ┌─ living-room-tv [ff:ee:dd:cc:bb:aa]
  │  connects:    8    disconnects: 12
  │  roaming:     2x   sta-timeout: 2x   kickouts: 5x
  │  lower-sig:   6x   rate-mismatch: 3x   auth-fail: 1x   reconnects: 0x
  │  signal:      avg -81 dBm   worst -92 dBm
  │  bands:       2.4GHz:6x  5GHz:2x
  │  ⚠  kicked 5x below -70dBm on 2.4GHz → move device closer or raise 2.4GHz TX power
  │  ⚠  blocked from 6GHz 3x → device not WiFi 6E capable
  │  ⚠  poor avg signal -81 dBm → too far from AP or obstructed
  └───────────────────────────────────────────────────────

  RANKINGS
  ─────────────────────────────────────────────────────
  Roaming:  macbook-pro: 11x  |  living-room-tv: 2x
  Signal:   living-room-tv: -81dBm avg  |  macbook-pro: -63dBm avg
  Needs attention: living-room-tv (score 17)
════════════════════════════════════════════════════════════════════
```

## Issue flags and what they mean

| Flag | Threshold | Meaning |
|---|---|---|
| roams Nx | > 5 events | Device in marginal coverage zone, bouncing between bands |
| STA timeout | any | Hard signal loss — device went out of range |
| kicked Nx below -70dBm | > 2 | AP band steering kicked device; consistently weak on 2.4GHz |
| kicked Nx for low signal | > 3 | At signal floor, connection unreliable |
| blocked from 6GHz | any | Device not WiFi 6E (802.11ax 6GHz) capable |
| poor avg signal | avg < -78 dBm | Too far from AP or physically obstructed |
| auth failures | any | Driver issue or credential/handshake problem |

## Why not always-running monitoring?

This tool is intentionally diagnostic-only. For persistent WiFi event monitoring,
send AP syslog to a SIEM (e.g. Wazuh) and write custom decoders/rules there.
