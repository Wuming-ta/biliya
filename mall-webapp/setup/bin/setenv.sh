#!/bin/sh
#dir=$(cd `dirname $0`; pwd)
dir=$(dirname $(readlink -f $0))
config=$(readlink -f $dir/../config/config.properties)

export JAVA_OPTS="$JAVA_OPTS -Djfeat.config.properties=$config"
#export CATALINA_OPTS=" -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"
export CATALINA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=*:5005,suspend=n"
