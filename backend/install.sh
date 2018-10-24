#!/bin/bash

set -e
mvn install
python3 -m pip install requests