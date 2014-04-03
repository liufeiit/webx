#!/bin/sh
cd ..
mvn clean package -Pprd -Dmaven.test.skip=true
