#!/bin/bash
cd "$(dirname "$0")"
# was: 4G, 16G
exec java -Xms512M -Xmx1G -XX:+UseFastAccessorMethods -XX:+AggressiveOpts -XX:+DisableExplicitGC -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:SurvivorRatio=16 -XX:+UseParallelGC -XX:UseSSE=3 -XX:ParallelGCThreads=8 -jar minecraft_server.jar nogui
