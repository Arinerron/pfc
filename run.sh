#!/bin/bash

set -e;

cd bin

if [ $# -eq 0 ]; then
    clear;
fi

java -cp .:$(echo ../lib/*.jar | tr ' ' ':'):$(echo */*.class | tr ' ' ':') Main $@

cd ..
