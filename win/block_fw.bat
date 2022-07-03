@echo off
set arg1=%1
echo "Parameter: %arg1%"
FOR /R %arg1% %%G IN (*.exe) do (
	Echo "Blocking: %%G"
	netsh advfirewall firewall add rule name="Block %%G" dir=out program="%%G" profile=any action=block
)