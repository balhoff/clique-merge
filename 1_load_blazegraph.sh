#!/bin/bash

# Make sure all the RDF files are in a folder called `data`
export JAVA_OPTS="-XX:+UseG1GC -Xmx12G"
./clique-merge/bin/clique-merge --properties=blazegraph.properties load data
