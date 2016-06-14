#!/usr/bin/python

import urllib2
import re

def mutationCount(text):
    p = re.compile(r'mutationCount=(\d+)')
    return int(p.search(text).group(1))

def transitive(update):
    changes = submitUpdate(update)
    print("Transitive changes: %d" % changes)
    if changes > 0: transitive(update)
    
def symmetric(update):
    changes = submitUpdate(update)
    print("Symmetric changes: %d" % changes)
    return changes

def computeSameAs():
    transitive("sameas-transitive.rq")
    changes = symmetric("sameas-symmetric.rq")
    if changes > 0: computeSameAs()
    
def computeEquivalentClass():
    transitive("equivclass-transitive.rq")
    changes = symmetric("equivclass-symmetric.rq")
    if changes > 0: computeEquivalentClass()
    
def submitUpdate(filename):
    req = urllib2.Request("http://localhost:9999/blazegraph/sparql", open(filename).read(), {'Content-Type': 'application/sparql-update'})
    result = urllib2.urlopen(req).read()
    return mutationCount(result)
    
def electLeaders():
    sameAsChanges = submitUpdate("remove_extra_sameas_leaders.rq")
    print("Removed sameAs links: %d" % sameAsChanges)
    equivClassChanges = submitUpdate("remove_extra_equiv_leaders.rq")
    print("Removed equivalentClass links: %d" % equivClassChanges)
    
def moveRelationships():
    sameAsChanges = submitUpdate("move-relationships-sameas.rq")
    print("sameAs changes: %d" % sameAsChanges)
    equivClassChanges = submitUpdate("move-relationships-equiv.rq")
    print("equivalentClass changes: %d" % equivClassChanges)
    
def main():
    print("Computing sameAs")
    computeSameAs()
    print("Computing equivalentClass")
    computeEquivalentClass()
    print("Electing leaders")
    electLeaders()
    moveRelationships()
    
if __name__ == "__main__":
    main()
