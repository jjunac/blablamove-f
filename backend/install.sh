#!/bin/bash

set -e
source ../utils.sh

parse_args $@

if [[ $CHAOS -eq 1 ]]; then
    mvn install -DskipTests -P chaos-monkey
else
    mvn install -DskipTests
fi