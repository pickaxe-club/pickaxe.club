#!/bin/sh
echo "Uploading week$WEEK..."
aws s3 cp "../week$WEEK.tar.gz" s3://archive.pickaxe.club/ --grants read=uri=http://acs.amazonaws.com/groups/global/AllUsers --region=us-east-1     
