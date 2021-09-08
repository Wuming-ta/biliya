#!/bin/sh
dir=$(cd `dirname $0`; pwd)
export JAVA_OPTS="$JAVA_OPTS -Djfeat.config.properties=$dir/../config/config.properties"
export JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
export JAVA_OPTS="$JAVA_OPTS -Dlogback.configurationFile=$dir/../config/logback.xml"

export CATALINA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8080"
