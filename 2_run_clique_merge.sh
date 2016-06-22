#!/bin/bash

export JAVA_OPTS="-XX:+UseG1GC -Xmx12G"
./clique-merge/bin/clique-merge --properties=blazegraph.properties merge
