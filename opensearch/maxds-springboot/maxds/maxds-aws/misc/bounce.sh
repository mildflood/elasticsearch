#!/bin/bash
cd /home/gitlab-runner/builds/cxpmyGoQ/0/dera/idap/maxds-aws
pid=`ps aux |grep [D]spring.*maxds-opensearch-$1.war | awk '{print $2}'`
echo pid $pid
if test -z "$pid"
then
        echo "No active maxds-opensearch instance is running."
else
        echo "Maxds-opensearch instance PID: \$pid"
        kill -9 $pid
fi
#kill -9 $(ps aux |grep [D]spring.*maxds-opensearch-$1.war | awk '{print $2}')
cp -r ./target/*.war ./maxds-opensearch-$1.war
chown gitlab-runner:gitlab-runner ./maxds-opensearch-$1.war
chown -R gitlab-runner:gitlab-runner ./target
java -jar -Dspring.profiles.active=$1 maxds-opensearch-ci.war | tee -a maxds-aws.log
chown gitlab-runner:gitlab-runner ./maxds-aws.log

