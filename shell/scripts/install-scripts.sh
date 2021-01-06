#!/bin/bash

read -p "Path to OpenVPN config:" -r
sed -i "s+/home/to/your/config.ovpn+$REPLY+" vpn

for filename in ./*; do
 if [ "$filename" != "$0" ]; then
   echo "Copy $filename to /usr/bin/"
   chmod +x $filename
   cp $filename /usr/bin/
 fi
done
