#!/usr/bin/env bash
# diskinfo.sh - Show drive info: PCIe speed/lanes/bandwidth, device name,
#               mount path, and LUKS container mapping.
#
# Usage: sudo ./diskinfo.sh
#
# Notes:
#   - Needs root for full LUKS / lspci / smartctl info.
#   - Works with NVMe, SATA (AHCI), and USB drives. PCIe info is only
#     meaningful for devices that sit on a PCIe bus (NVMe, AHCI controllers,
#     RAID HBAs). For USB/SATA-on-chipset, "PCIe" refers to the controller.

set -u

# --- pretty colors (only if stdout is a TTY) -----------------------------
if [[ -t 1 ]]; then
    BOLD=$'\e[1m'; DIM=$'\e[2m'; RED=$'\e[31m'; GREEN=$'\e[32m'
    YELLOW=$'\e[33m'; BLUE=$'\e[34m'; CYAN=$'\e[36m'; RESET=$'\e[0m'
else
    BOLD=""; DIM=""; RED=""; GREEN=""; YELLOW=""; BLUE=""; CYAN=""; RESET=""
fi

# --- prerequisite check --------------------------------------------------
need() {
    command -v "$1" >/dev/null 2>&1 || {
        echo "${RED}Missing tool:${RESET} $1 ($2)" >&2
        return 1
    }
}
need lsblk  "util-linux"        || exit 1
need lspci  "pciutils"          || exit 1
need udevadm "systemd/udev"     || exit 1
# cryptsetup, smartctl, nvme are optional but enrich output
HAS_CRYPTSETUP=0; command -v cryptsetup >/dev/null 2>&1 && HAS_CRYPTSETUP=1
HAS_SMARTCTL=0;   command -v smartctl   >/dev/null 2>&1 && HAS_SMARTCTL=1
HAS_NVME=0;       command -v nvme       >/dev/null 2>&1 && HAS_NVME=1

if [[ $EUID -ne 0 ]]; then
    echo "${YELLOW}Warning:${RESET} not running as root. Some info (LUKS, SMART) may be missing." >&2
fi

# --- helpers -------------------------------------------------------------

# Compute theoretical PCIe bandwidth (one direction, GB/s) for a given
# generation and link width.  Values use the standard encoding overhead
# (Gen1/2: 8b/10b, Gen3+: 128b/130b).
pcie_bandwidth_mb() {
    local gen="$1" width="$2"
    local per_lane_gbps   # gigabits per second, after encoding
    case "$gen" in
        1) per_lane_gbps=2;        ;;   # 2.5 GT/s * 8/10
        2) per_lane_gbps=4;        ;;   # 5 GT/s   * 8/10
        3) per_lane_gbps="7.877";  ;;   # 8 GT/s   * 128/130
        4) per_lane_gbps="15.754"; ;;   # 16 GT/s
        5) per_lane_gbps="31.508"; ;;   # 32 GT/s
        6) per_lane_gbps="63.015"; ;;   # 64 GT/s (PAM4 + FLIT)
        *) echo "?"; return ;;
    esac
    # MB/s = gbps * width * 1000 / 8
    awk -v g="$per_lane_gbps" -v w="$width" \
        'BEGIN { printf "%.0f", g * w * 1000 / 8 }'
}

# Walk up the sysfs device tree from a block device until we hit a PCI slot.
# Echoes the PCI BDF (e.g. "0000:01:00.0") or empty string.
find_pci_parent() {
    local dev="$1"
    local syspath
    syspath=$(udevadm info -q path -n "$dev" 2>/dev/null) || return
    # syspath is e.g. /devices/pci0000:00/0000:00:1d.0/0000:01:00.0/nvme/nvme0/nvme0n1
    local cur="/sys$syspath"
    while [[ -n "$cur" && "$cur" != "/sys" && "$cur" != "/" ]]; do
        local base; base=$(basename "$cur")
        if [[ "$base" =~ ^[0-9a-fA-F]{4}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}\.[0-9a-fA-F]+$ ]]; then
            echo "$base"
            return
        fi
        cur=$(dirname "$cur")
    done
}

# Map PCIe speed string ("8.0 GT/s", "8 GT/s", "16.0 GT/s PCIe", etc.) -> Gen number
pcie_gen_from_speed() {
    local s="$1"
    # extract first numeric token
    local n; n=$(grep -oE "[0-9]+(\.[0-9]+)?" <<<"$s" | head -n1)
    case "$n" in
        2.5)     echo 1 ;;
        5|5.0)   echo 2 ;;
        8|8.0)   echo 3 ;;
        16|16.0) echo 4 ;;
        32|32.0) echo 5 ;;
        64|64.0) echo 6 ;;
        *)       echo "?" ;;
    esac
}

# Read PCIe link info from sysfs (works WITHOUT root).
# Echoes 4 space-separated tokens: cur_gen cur_width max_gen max_width
# Empty fields become "?".
pcie_info_sysfs() {
    local bdf="$1"
    local base="/sys/bus/pci/devices/$bdf"
    [[ -d "$base" ]] || { echo "? ? ? ?"; return; }

    local cs cw ms mw cur_gen max_gen
    cs=$(cat "$base/current_link_speed" 2>/dev/null)
    cw=$(cat "$base/current_link_width" 2>/dev/null)
    ms=$(cat "$base/max_link_speed"     2>/dev/null)
    mw=$(cat "$base/max_link_width"     2>/dev/null)

    cur_gen=$(pcie_gen_from_speed "$cs")
    max_gen=$(pcie_gen_from_speed "$ms")

    echo "${cur_gen:-?} ${cw:-?} ${max_gen:-?} ${mw:-?}"
}

# Print PCIe info for a BDF.  Uses sysfs (root-free) and also reports the
# controller's MAX capability so you can tell whether a drive is link-capped.
pcie_info() {
    local bdf="$1"
    [[ -z "$bdf" ]] && { echo "n/a"; return; }

    local name; name=$(lspci -s "$bdf" 2>/dev/null | sed "s/^${bdf} //")
    [[ -z "$name" ]] && name="(unknown PCI device)"

    read -r cgen cwidth mgen mwidth < <(pcie_info_sysfs "$bdf")

    if [[ "$cgen" == "?" && "$mgen" == "?" ]]; then
        # Truly nothing — chipset-internal device with no PCIe link concept
        printf "(no PCIe link info)  [%s]  %s" "$bdf" "$name"
        return
    fi

    local cbw="?" mbw="?"
    [[ "$cgen" != "?" && "$cwidth" != "?" ]] && cbw=$(pcie_bandwidth_mb "$cgen" "$cwidth")
    [[ "$mgen" != "?" && "$mwidth" != "?" ]] && mbw=$(pcie_bandwidth_mb "$mgen" "$mwidth")

    # If current == max, just print one line. Otherwise show both.
    if [[ "$cgen" == "$mgen" && "$cwidth" == "$mwidth" ]]; then
        printf "Gen%s x%s  (%s MB/s)  [%s]  %s" \
               "$cgen" "$cwidth" "$cbw" "$bdf" "$name"
    else
        printf "Gen%s x%s  (%s MB/s)  [current; max Gen%s x%s = %s MB/s]  [%s]  %s" \
               "$cgen" "$cwidth" "$cbw" "$mgen" "$mwidth" "$mbw" "$bdf" "$name"
    fi
}

# For SATA disks, the PCIe link of the AHCI controller is rarely the
# bottleneck and often not even reported (chipset-internal). The meaningful
# "speed" is the SATA link speed of the disk itself.
#
# Strategy:
#   1) From the block device's sysfs path, walk up looking for an "ataN" dir.
#   2) Inside that, look at "link*/ata_link*/sata_spd" (current negotiated)
#      and "link*/ata_link*/sata_spd_limit" (firmware/bios cap).
#   3) Fall back to globbing /sys/class/ata_link/.
#   4) Final fallback: smartctl -i (needs root).
#
# Echoes one line like:
#   "SATA 6.0 Gb/s negotiated (~600 MB/s); port supports up to 6.0 Gb/s"
sata_link_info() {
    local dev="$1"
    local base; base=$(basename "$dev")

    local cur="" cap=""

    # Walk up from /sys/class/block/<dev>/device toward the ata host
    local syspath; syspath=$(readlink -f "/sys/class/block/$base/device" 2>/dev/null)
    if [[ -n "$syspath" ]]; then
        local p="$syspath"
        local ata_host=""
        while [[ -n "$p" && "$p" != "/" ]]; do
            # ataN directory itself (host)
            local cand
            cand=$(ls -d "$p"/ata[0-9]* 2>/dev/null | head -n1)
            if [[ -n "$cand" ]]; then ata_host="$cand"; break; fi
            # Or we might already be inside one
            if [[ "$(basename "$p")" =~ ^ata[0-9]+$ ]]; then ata_host="$p"; break; fi
            p=$(dirname "$p")
        done
        if [[ -n "$ata_host" ]]; then
            # link node lives at ataN/linkM/  (and also as ata_link inside)
            local link_dir
            link_dir=$(ls -d "$ata_host"/link[0-9]* 2>/dev/null | head -n1)
            if [[ -n "$link_dir" ]]; then
                # Try direct files first
                [[ -r "$link_dir/sata_spd"       ]] && cur=$(<"$link_dir/sata_spd")
                [[ -r "$link_dir/sata_spd_limit" ]] && cap=$(<"$link_dir/sata_spd_limit")
                # Or via the ata_link subdir (newer kernels)
                if [[ -z "$cur" ]]; then
                    local al; al=$(ls -d "$link_dir"/ata_link/link[0-9]* 2>/dev/null | head -n1)
                    [[ -n "$al" && -r "$al/sata_spd"       ]] && cur=$(<"$al/sata_spd")
                    [[ -n "$al" && -r "$al/sata_spd_limit" ]] && cap=$(<"$al/sata_spd_limit")
                fi
            fi
        fi
    fi

    # Fallback: smartctl
    if [[ -z "$cur" && $HAS_SMARTCTL -eq 1 && $EUID -eq 0 ]]; then
        local sm; sm=$(smartctl -i "$dev" 2>/dev/null)
        cur=$(awk -F'current:' '/SATA Version/ {gsub(/^ +| +$/,"",$2); print $2; exit}' <<<"$sm")
        # SATA Version line also has the device's max: "SATA Version is: SATA 3.2, 6.0 Gb/s (current: 6.0 Gb/s)"
        if [[ -z "$cap" ]]; then
            cap=$(awk -F'is:' '/SATA Version/ {print $2; exit}' <<<"$sm" \
                  | grep -oE "[0-9.]+ Gb/s" | head -n1)
        fi
    fi

    # Filter out non-numeric "limit" values like "<unknown>" — those mean
    # "no firmware-imposed cap reported", not an actual cap.
    if [[ -n "$cap" && ! "$cap" =~ [0-9] ]]; then
        cap=""
    fi

    [[ -z "$cur" && -z "$cap" ]] && { echo ""; return; }

    # Compute MB/s for the negotiated link (8b/10b => *0.8)
    local mb="?"
    if [[ -n "$cur" ]]; then
        mb=$(awk -v s="$cur" 'BEGIN { n=s+0; if (n>0) printf "%.0f", n*1000*0.8/8; else printf "?"; }')
    fi

    if [[ -n "$cur" && -n "$cap" && "$cur" != "$cap" ]]; then
        echo "SATA $cur negotiated (~${mb} MB/s); link capped at $cap"
    elif [[ -n "$cur" ]]; then
        echo "SATA $cur (~${mb} MB/s)"
    else
        echo "SATA cap: $cap"
    fi
}

# Render the full block-device tree under a disk, showing the chain from
# partition -> LUKS -> LVM -> filesystem with mount points. This correctly
# handles the case where a LUKS container holds an LVM PV.
#
# We use lsblk's tree mode (default) but ask only for the columns we want.
# The TYPE column tells us what each node is:
#   disk, part, crypt, lvm, raid*, dm-*, etc.
# We mark "crypt" nodes specially so it's clear what's encrypted.
device_tree() {
    local dev="$1"
    # -p: full paths, no -r so we keep tree characters
    lsblk -po NAME,TYPE,FSTYPE,SIZE,MOUNTPOINTS "$dev" 2>/dev/null \
        | awk 'NR>1'    # drop header; first body line is the disk itself
}

# Returns 0 if any node under this disk is encrypted (TYPE=crypt) or has a
# LUKS-typed partition; 1 otherwise.
has_luks() {
    lsblk -prno TYPE,FSTYPE "$1" 2>/dev/null \
        | awk '$1=="crypt" || $2=="crypto_LUKS" {found=1} END{exit !found}'
}

# Pretty model/serial via udev (works without root for most attrs)
device_model() {
    local dev="$1"
    udevadm info --query=property --name="$dev" 2>/dev/null \
        | awk -F= '
            $1=="ID_MODEL"           {model=$2}
            $1=="ID_VENDOR"          {vendor=$2}
            $1=="ID_SERIAL_SHORT"    {serial=$2}
            END {
                if (vendor && model) printf "%s %s", vendor, model;
                else if (model)      printf "%s", model;
                else                 printf "(unknown model)";
                if (serial) printf "  S/N: %s", serial;
            }'
}

# --- main loop -----------------------------------------------------------

# Get list of physical disks (TYPE=disk), excluding loop/zram/ram/dm devices.
mapfile -t DISKS < <(
    lsblk -dpno NAME,TYPE,RM,SIZE \
        | awk '$2=="disk" {print $1}' \
        | grep -Ev '^/dev/(loop|zram|ram|sr|fd)' \
        || true
)

if (( ${#DISKS[@]} == 0 )); then
    echo "${RED}No physical disks found.${RESET}" >&2
    exit 1
fi

echo "${BOLD}${CYAN}Hard drives on this system${RESET}"
echo "${DIM}$(date)  -  $(uname -srm)${RESET}"
echo

for dev in "${DISKS[@]}"; do
    size=$(lsblk -dno SIZE "$dev" 2>/dev/null)
    tran=$(lsblk -dno TRAN "$dev" 2>/dev/null)
    rota=$(lsblk -dno ROTA "$dev" 2>/dev/null)
    [[ "$rota" == "1" ]] && kind="HDD" || kind="SSD"
    [[ -z "$tran" ]] && tran="?"

    echo "${BOLD}${BLUE}== $dev  (${size:-?}, $kind, $tran) ==${RESET}"
    echo "  ${BOLD}Model :${RESET} $(device_model "$dev")"

    bdf=$(find_pci_parent "$dev")

    if [[ "$tran" == "sata" ]]; then
        # For SATA drives, the controller's "PCIe link" reported by sysfs is
        # really the chipset uplink shared by everything on the controller —
        # not a per-drive bottleneck. Show only the controller name and let
        # the SATA Link line below show the meaningful number.
        if [[ -n "$bdf" ]]; then
            ctrl=$(lspci -s "$bdf" 2>/dev/null | sed "s/^${bdf} //")
            echo "  ${BOLD}Ctrl  :${RESET} [$bdf] ${ctrl:-(unknown)}"
        fi
        sata=$(sata_link_info "$dev")
        if [[ -n "$sata" ]]; then
            echo "  ${BOLD}Link  :${RESET} $sata"
        fi
    else
        # NVMe and other PCIe-attached devices: per-drive PCIe link IS the link.
        echo "  ${BOLD}PCIe  :${RESET} $(pcie_info "$bdf")"
    fi

    # Unified layout tree (partitions -> LUKS -> LVM -> filesystems)
    echo "  ${BOLD}Layout:${RESET}"
    device_tree "$dev" | sed 's/^/    /'

    # If there are any LUKS containers, summarize them (cipher etc.)
    if has_luks "$dev"; then
        if [[ $HAS_CRYPTSETUP -eq 1 ]]; then
            printed_header=0
            while read -r part; do
                if cryptsetup isLuks "$part" 2>/dev/null; then
                    if [[ $printed_header -eq 0 ]]; then
                        echo "  ${BOLD}LUKS  :${RESET}"
                        printed_header=1
                    fi
                    if [[ $EUID -eq 0 ]]; then
                        cipher=$(cryptsetup luksDump "$part" 2>/dev/null \
                                 | awk -F: '/cipher:/ {gsub(/^ +/,"",$2); print $2; exit}')
                        [[ -z "$cipher" ]] && cipher="?"
                        # Find the open mapper name (if any) for this LUKS device
                        mapper=$(lsblk -prno NAME,TYPE "$part" 2>/dev/null \
                                 | awk '$2=="crypt"{print $1; exit}')
                        [[ -z "$mapper" ]] && mapper="(closed)"
                        echo "    $part  cipher=$cipher  -> $mapper"
                    else
                        echo "    $part  (run as root for cipher details)"
                    fi
                fi
            done < <(lsblk -rnpo NAME,TYPE "$dev" | awk '$2=="part"{print $1}')
        fi
    fi

    echo
done
