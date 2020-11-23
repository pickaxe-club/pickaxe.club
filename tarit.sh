#!/bin/sh
echo "Backing up week$WEEK..."
tar --use-compress-program=pigz --exclude='./.git' --exclude="*.tar.gz" -cvf ../week$WEEK.tar.gz .
