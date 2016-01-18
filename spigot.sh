#!/bin/bash
cd "$(dirname "$0")"
exec java -Xms4G -Xmx16G -XX:MaxPermSize=128M -XX:+UseFastAccessorMethods -XX:+AggressiveOpts -XX:+DisableExplicitGC -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:SurvivorRatio=16 -XX:+UseParallelGC -XX:UseSSE=3 -XX:ParallelGCThreads=8 -jar spigot_server.jar nogui
