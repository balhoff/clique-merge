#!/bin/bash

curl -O http://tenet.dl.sourceforge.net/project/bigdata/bigdata/2.1.1/blazegraph.jar

# Make sure all the RDF files are in a folder called `data`
java -XX:+UseG1GC -Xmx8G -cp blazegraph.jar com.bigdata.rdf.store.DataLoader blazegraph.properties data
