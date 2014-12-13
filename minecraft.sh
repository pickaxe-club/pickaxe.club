#!/bin/bash
cd "$(dirname "$0")"
exec java -Xms1G -Xmx2G -jar minecraft_server.jar nogui
