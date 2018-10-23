#!/bin/bash

for f in $(ls -d */)
do
    if [ -f $f/test.sh ]; then
        echo "===== Entering $f ====="
        cd $f
        chmod u+x ./test.sh
        ./test.sh
        return_code=$?
        if [ $return_code -ne 0 ];then
            exit $return_code
        fi
        cd ..
    fi
done