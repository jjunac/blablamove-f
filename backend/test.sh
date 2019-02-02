#!/usr/bin/env bash

set -e
function cleanup {
    docker-compose -f ../docker-compose.yml down
}
trap cleanup EXIT
mvn package
docker-compose -f ../docker-compose.yml up -d --force-recreate
docker-compose logs -f &
sleep 65
echo "===== Starting integration test ====="
python3 -m pip install --user requests==2.20.0
python3 webservice_tests.py
echo "===== Test succeeded ====="