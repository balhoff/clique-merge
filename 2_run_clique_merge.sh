#!/bin/bash

export JAVA_OPTS="-Xmx12G"
./clique-merge/bin/clique-merge --properties=blazegraph.properties merge
