#!/bin/bash

for f in $(ls -d */)
do
    if [ -f $f/test.sh ]; then
        echo "entering $f"
        cd $f
        ./test.sh
        cd ..
    fi
done