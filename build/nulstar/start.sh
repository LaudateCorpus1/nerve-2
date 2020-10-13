#!/bin/bash
cd `dirname $0`
export NERVE_STOP_FILE=`pwd`/.nerve-stop
CONFIG="./nuls.ncf"
if [ ! -f "${CONFIG}" ] ; then
        mv ./.default-config.ncf nuls.ncf
        echo "nuls.ncf is created by default-config.ncf."
        echo "Please re-excute the startup program."
        exit 0
fi
if [ -f $NERVE_STOP_FILE ]; then
   rm -f $NERVE_STOP_FILE
fi
LD_LIBRARY_PATH=Libraries/CPP/Nulstar/0.1.0:Libraries/CPP/Qt/5.12.3 `pwd`/Modules/Nulstar/Nulstar/0.1.0/Nulstar &
