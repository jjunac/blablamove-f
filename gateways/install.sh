#!/bin/bash
source ../utils.sh

set -e

parse_args $@

for d in $(ls -d */)
do
    cd $d
	if [[ $CHAOS -eq 1 ]]; then
        mvn clean install -DskipTests -P chaos-monkey
    else
        mvn clean install -DskipTests
    fi
    cd ..
done
