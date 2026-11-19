#!/bin/sh
# Starts Tomcat on the port the hosting platform gives us (PORT), default 8080.
CONF=/usr/local/tomcat/conf/server.xml
sed -i "s/port=\"8080\"/port=\"${PORT:-8080}\"/" "$CONF"
# Trust X-Forwarded-* headers from the platform's HTTPS proxy
sed -i 's#</Host>#  <Valve className="org.apache.catalina.valves.RemoteIpValve" />\n      </Host>#' "$CONF"
exec catalina.sh run
