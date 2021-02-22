#!/bin/sh
echo "Backing up week$WEEK..."
git add -A .; git commit -m "Weekend $WEEK final" 
# git push origin master -- no key available for pushing at this time. UncleAdam
tar --use-compress-program=pigz --exclude="*.tar.gz" -cvf ../week$WEEK.tar.gz .
aws s3 cp "../week$WEEK.tar.gz" s3://archive.pickaxe.club/ --grants read=uri=http://acs.amazonaws.com/groups/global/AllUsers --region=us-east-1     
