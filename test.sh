#!/bin/bash

set -e

for f in $(ls -d */)
do
    if [ -f $f/test.sh ]; then
        echo "===== Entering $f ====="
        cd $f
        bash test.sh
        cd ..
    fi
done