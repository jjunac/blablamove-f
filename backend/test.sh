#!/usr/bin/env bash

mvn package && \
    java -jar target/blablamove-1.0-SNAPSHOT.jar & && \
    sleep 10 && \
    python webservice_tests.py