#!/bin/bash

JMX_PORT=18001
GC_LOG=./logs/gc.log
#jvm config
JAVA_BASE_OPTS=" -Djava.awt.headless=true -Dfile.encoding=UTF-8 "

JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=$JMX_PORT \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false "

JAVA_MEM_OPTS=" -server -Xms2g -Xmx2g -Xmn600m -XX:PermSize=128m \
-XX:MaxPermSize=128m -Xss256K \
-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled \
-XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m \
-XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly \
-XX:CMSInitiatingOccupancyFraction=70 "

JAVA_GC_OPTS=" -verbose:gc -Xloggc:$GC_LOG \
-XX:+PrintGCDetails -XX:+PrintGCDateStamps "

JAVA_CP=" -cp conf:lib/* "

JAVA_OPTS=" $JAVA_BASE_OPTS $JAVA_MEM_OPTS $JAVA_JMX_OPTS $JAVA_GC_OPTS $JAVA_CP"

RUNJAVA="$JAVA_HOME/bin/java"

$RUNJAVA $JAVA_CP com.github.wenweihu86.distkv.proxy.ProxyMain
