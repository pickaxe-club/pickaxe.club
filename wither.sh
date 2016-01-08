#!/bin/bash

tail -F /home/minecraft/pickaxe.club/logs/latest.log | grep --line-buffered ": <" | while read x ; do echo -ne $x | curl -X POST -d @- https://wither.herokuapp.com/minecraft/hook ; done
