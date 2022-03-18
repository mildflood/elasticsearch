#!/bin/bash
cd /apps/jenkins/jenkins1/.jenkins/workspace/maxds-aws
ps aux | grep [D]spring.*maxds-aws-$1.war | awk '{print $2}'
kill -9 $(ps aux | grep [D]spring.*maxds-aws-$1.war | awk '{print $2}')
cp -r ./target/*.war ./maxds-aws-$1.war
java -jar -Dspring.profiles.active=$1 maxds-aws-$1.war > maxds-aws.log &
