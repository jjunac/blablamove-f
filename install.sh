#!/bin/bash

set -e
source utils.sh

parse_args $@

for f in "chaos_monkey_java" "backend" "gateways"
do
    echo $f
    if [ -f $f/install.sh ]; then
        echo "entering $f"
        cd $f
        bash install.sh $@
        cd ..
    fi
done
docker-compose build