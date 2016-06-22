package org.monarchinitiative.clique

import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader
import java.util.Properties
import java.util.stream.Collectors

import org.backuity.clist._
import org.openrdf.query.QueryLanguage

import com.bigdata.rdf.sail.BigdataSail
import com.bigdata.rdf.sail.BigdataSailRepository
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection

object Merge extends Command(description = "Perform clique merge on triples in Blazegraph.") with Common {

  override def run(): Unit = {
    val blazegraphProperties = new Properties()
    blazegraphProperties.load(new FileReader(properties))
    val sail = new BigdataSail(blazegraphProperties)
    val repository = new BigdataSailRepository(sail)
    repository.initialize()
    val blazegraph = repository.getUnisolatedConnection
    println("Converting equivalentClass to sameAs")
    convertEquivToSameAs(blazegraph)
    println("Computing sameAs")
    computeSameAs(blazegraph)
    println("Electing leaders")
    electLeaders(blazegraph)
    println("Moving relationships")
    moveRelationships(blazegraph)
    blazegraph.close()
  }

  def convertEquivToSameAs(connection: BigdataSailRepositoryConnection): Unit = {
    val changes = submitUpdate(getQuery("convert-equiv-to-sameas.rq"), connection)
    println(s"Convert equiv to sameAs changes: $changes")
  }

  def computeSameAs(connection: BigdataSailRepositoryConnection): Unit = {
    transitive(getQuery("sameas-transitive.rq"), connection)
    val changes = submitUpdate(getQuery("sameas-symmetric.rq"), connection)
    println(s"Symmetric changes: $changes")
    if (changes > 0) computeSameAs(connection)
  }

  def transitive(update: String, connection: BigdataSailRepositoryConnection): Unit = {
    val changes = submitUpdate(update, connection)
    println(s"Transitive changes: $changes")
    if (changes > 0) transitive(update, connection)
  }

  def electLeaders(connection: BigdataSailRepositoryConnection): Unit = {
    val changes = submitUpdate(getQuery("elect_leaders.rq"), connection)
    println(s"Removed sameAs links: $changes")
  }

  def moveRelationships(connection: BigdataSailRepositoryConnection): Unit = {
    val outgoingChanges = submitUpdate(getQuery("move-outgoing-relationships-sameas.rq"), connection)
    println(s"sameAs changes outgoing: $outgoingChanges")
    val incomingChanges = submitUpdate(getQuery("move-incoming-relationships-sameas.rq"), connection)
    println(s"sameAs changes incoming: $incomingChanges")
  }

  private def submitUpdate(update: String, connection: BigdataSailRepositoryConnection): Int = {
    val mutationCounter = new MutationCounter()
    connection.addChangeLog(mutationCounter)
    connection.prepareUpdate(QueryLanguage.SPARQL, update).execute()
    val mutations = mutationCounter.mutationCount
    connection.removeChangeLog(mutationCounter)
    mutations
  }

  private def getQuery(filename: String): String =
    new BufferedReader(new InputStreamReader(this.getClass.getResourceAsStream(filename))).lines.collect(Collectors.joining("\n"))

}