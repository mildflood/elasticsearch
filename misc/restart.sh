#!/bin/bash
cd /apps/jenkins/jenkins1/.jenkins/workspace/maxds-aws
ps aux | grep [D]spring.maxds-aws-0.0.1-SNAPSHOT.war | awk '{print $2}'
kill -9 $(ps aux | grep [D]spring.maxdsaws-0.0.1-SNAPSHOT.war | awk '{print $2}')
cp -rvf ./target/*.war ./maxds-aws-0.0.1-SNAPSHOT.war
java -jar -Dspring.profiles.active=$1 maxds-aws-0.0.1-SNAPSHOT.war &