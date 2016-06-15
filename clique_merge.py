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
    
def convertEquivToSameAs():
    changes = submitUpdate("convert-equiv-to-sameas.rq")
    print("Convert equiv to sameAs changes: %d" % changes)
    
def submitUpdate(filename):
    req = urllib2.Request("http://localhost:9999/blazegraph/sparql", open(filename).read(), {'Content-Type': 'application/sparql-update'})
    result = urllib2.urlopen(req).read()
    return mutationCount(result)
    
def electLeaders():
    sameAsChanges = submitUpdate("elect_leaders.rq")
    print("Removed sameAs links: %d" % sameAsChanges)
    
def moveRelationships():
    sameAsChangesOutgoing = submitUpdate("move-outgoing-relationships-sameas.rq")
    print("sameAs changes outgoing: %d" % sameAsChangesOutgoing)
    sameAsChangesIncoming = submitUpdate("move-incoming-relationships-sameas.rq")
    print("sameAs changes incoming: %d" % sameAsChangesIncoming)
    
def main():
    print("Converting equivalentClass to sameAs")
    convertEquivToSameAs()
    print("Computing sameAs")
    computeSameAs()
    print("Electing leaders")
    electLeaders()
    print("Moving relationships")
    moveRelationships()
    
if __name__ == "__main__":
    main()
