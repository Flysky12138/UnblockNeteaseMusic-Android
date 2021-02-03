#!/system/bin/sh

start() {
echo "
base {
   log_debug = off;
   log_info = off;
   log = stderr;
   daemon = on;
   redirector = iptables;
}
redsocks {
   local_ip = 0.0.0.0;
   local_port = 8123;
   ip = 127.0.0.1;
   port = 8080;
   type = http-relay;
}" > ./redsocks.conf
    re=$(grep -m1 -i "com.netease.cloudmusic" /data/system/packages.list | cut -d' ' -f2)
    re2=$(grep -m1 -i "com.netease.cloudmusic.lite" /data/system/packages.list | cut -d' ' -f2)
    ./redsocks -c ./redsocks.conf
    iptables -t nat -A OUTPUT -p tcp -m owner --uid-owner $re -m tcp --dport 80 -j REDIRECT --to-ports 8123
    iptables -t nat -A OUTPUT -p tcp -m owner --uid-owner $re -m tcp --dport 443 -j REDIRECT --to-ports 8124
    iptables -t nat -A OUTPUT -p tcp -m owner --uid-owner $re2 -m tcp --dport 80 -j REDIRECT --to-ports 8123
    iptables -t nat -A OUTPUT -p tcp -m owner --uid-owner $re2 -m tcp --dport 443 -j REDIRECT --to-ports 8124
}
stop() {
    iptables -t nat -F OUTPUT
    iptables -t nat -F PREROUTING
    killall -9 redsocks >/dev/null 2>&1
}
state() {
    iptables -t nat -S OUTPUT
}

if [ "$1" = "start" ];then
    stop
    start
elif [ "$1" = "stop" ];then
    stop
fi
state