#!/usr/bin/env bash

set -e

mvn package
java -jar target/blablamove-1.0-SNAPSHOT.jar &
sleep 10
python webservice_tests.py