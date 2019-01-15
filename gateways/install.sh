#!/bin/bash

for d in $(ls -d */)
do
	mvn clean install -f $d/pom.xml
done