#!/bin/bash

set -e
source ../utils.sh

parse_args $@

mvn install -DskipTests