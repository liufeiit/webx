#!/bin/sh
cd ..
mvn clean install -Pprd -Dmaven.test.skip=true
cp /home/lf/workspace/scheduler/target/scheduler.war /home/lf/dev/jboss-as-7.1.1.Final/standalone/deployments/

