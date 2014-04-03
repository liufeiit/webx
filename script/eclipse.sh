#!/bin/sh
cd ..
mvn eclipse:clean
mvn eclipse:eclipse -DdwonloadSources=true -DdownloadJavadocs=true
