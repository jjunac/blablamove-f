#!/usr/bin/env bash

set -e

mvn package
docker-compose -f ../docker-compose.yml up -d
sleep 5
java -jar target/blablamove-1.0-SNAPSHOT.jar &
sleep 10
python3 -m pip install --user requests==2.20.0
python3 webservice_tests.py