#!/bin/bash

# These are taken from the Monarch scigraph-data config.
# You need owltools installed for getting the imports of the ontologies.

set -e

mkdir -p data
cd data
mkdir -p ontologies

curl -L -O http://data.monarchinitiative.org/owl/monarch-merged.owl
owltools http://biohackathon.org/resource/faldo.ttl --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools http://purl.obolibrary.org/obo/eco.owl --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools http://purl.obolibrary.org/obo/iao.owl --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools https://raw.githubusercontent.com/monarch-initiative/GENO-ontology/develop/src/ontology/geno.owl --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools http://data.monarchinitiative.org/owl/ero.owl --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools http://purl.obolibrary.org/obo/pw.owl --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools https://raw.githubusercontent.com/jamesmalone/OBAN/master/ontology/oban_core.ttl --slurp-import-closure -d ontologies -c catalog-v001.xml
# Blazegraph will try to load .owl as RDF/XML
mv ontologies/purl.org/oban/oban_core.owl ontologies/purl.org/oban/oban_core.ttl
## change to RDF/XML?
  # clo.owl is missing and import
  #- url: http://purl.obolibrary.org/obo/clo.owl
owltools http://purl.obolibrary.org/obo/pco.owl --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools http://purl.obolibrary.org/obo/xco.owl --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools http://xmlns.com/foaf/spec/index.rdf --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools http://purl.org/dc/elements/1.1/ --slurp-import-closure -d ontologies -c catalog-v001.xml
owltools http://xmlns.com/foaf/0.1/ --slurp-import-closure -d ontologies -c catalog-v001.xml

curl -L -O http://data.monarchinitiative.org/ttl/mgi.ttl
curl -L -O http://data.monarchinitiative.org/ttl/panther.ttl
curl -L -O http://data.monarchinitiative.org/ttl/clinvar.ttl
curl -L -O http://data.monarchinitiative.org/ttl/ncbigene.ttl
curl -L -O http://data.monarchinitiative.org/ttl/biogrid.ttl
curl -L -O http://data.monarchinitiative.org/ttl/zfin.ttl
curl -L -O http://data.monarchinitiative.org/ttl/ctd.ttl
curl -L -O http://data.monarchinitiative.org/ttl/hpoa.ttl
curl -L -O http://data.monarchinitiative.org/ttl/omim.ttl
curl -L -O http://data.monarchinitiative.org/ttl/coriell.ttl
curl -L -O http://data.monarchinitiative.org/ttl/hgnc.ttl
curl -L -O http://data.monarchinitiative.org/ttl/kegg.ttl
curl -L -O http://data.monarchinitiative.org/ttl/impc.ttl
curl -L -O http://data.monarchinitiative.org/ttl/ucscbands.ttl
#curl -L -O http://data.monarchinitiative.org/ttl/cgd.ttl    # this is not ready yet
curl -L -O http://data.monarchinitiative.org/ttl/monochrom.ttl
curl -L -O http://data.monarchinitiative.org/ttl/eom.ttl
curl -L -O http://data.monarchinitiative.org/ttl/genereviews.ttl
curl -L -O http://data.monarchinitiative.org/ttl/orphanet.ttl
curl -L -O http://data.monarchinitiative.org/ttl/animalqtldb.ttl
curl -L -O http://data.monarchinitiative.org/ttl/flybase.nt
curl -L -O http://data.monarchinitiative.org/ttl/wormbase.nt
curl -L -O http://data.monarchinitiative.org/ttl/omia.ttl
curl -L -O http://data.monarchinitiative.org/ttl/mmrrc.ttl
curl -L -O http://data.monarchinitiative.org/ttl/gwascatalog.ttl
curl -L -O http://data.monarchinitiative.org/ttl/monarch.ttl
curl -L -O http://data.monarchinitiative.org/ttl/mpd.ttl
curl -L -O http://data.monarchinitiative.org/ttl/go.ttl

# Blazegraph will error if it tries to load this
rm ontologies/catalog-v001.xml
