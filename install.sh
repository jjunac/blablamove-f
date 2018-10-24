#!/bin/bash

for f in $(ls -d */)
do
    if [ -f $f/install.sh ]; then
        echo "entering $f"
        cd $f
        bash install.sh
        cd ..
    fi
done