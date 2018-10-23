#!/bin/bash

for f in $(ls -d */)
do
    if [ -f $f/install.sh ]; then
        echo "entering $f"
        cd $f
        ./install.sh
        cd ..
    fi
done