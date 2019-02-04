#!/bin/bash

source ../utils.sh

parse_args $@

for d in $(ls -d */)
do
	mvn clean install -f $d/pom.xml
done