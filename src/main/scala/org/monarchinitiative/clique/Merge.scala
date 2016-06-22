package org.monarchinitiative.clique

import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader
import java.util.Properties
import java.util.stream.Collectors

import org.backuity.clist._
import org.openrdf.query.QueryLanguage
import org.slf4j.LoggerFactory

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
    logger.info("Converting equivalentClass to sameAs")
    convertEquivToSameAs(blazegraph)
    logger.info("Computing sameAs")
    computeSameAs(blazegraph)
    logger.info("Electing leaders")
    electLeaders(blazegraph)
    logger.info("Moving relationships")
    moveRelationships(blazegraph)
    blazegraph.close()
  }

  def convertEquivToSameAs(connection: BigdataSailRepositoryConnection): Unit = {
    val changes = submitUpdate(getQuery("convert-equiv-to-sameas.rq"), connection)
    logger.info(s"Convert equiv to sameAs changes: $changes")
  }

  def computeSameAs(connection: BigdataSailRepositoryConnection): Unit = {
    transitive(getQuery("sameas-transitive.rq"), connection)
    val changes = submitUpdate(getQuery("sameas-symmetric.rq"), connection)
    logger.info(s"Symmetric changes: $changes")
    if (changes > 0) computeSameAs(connection)
  }

  def transitive(update: String, connection: BigdataSailRepositoryConnection): Unit = {
    val changes = submitUpdate(update, connection)
    logger.info(s"Transitive changes: $changes")
    if (changes > 0) transitive(update, connection)
  }

  def electLeaders(connection: BigdataSailRepositoryConnection): Unit = {
    val changes = submitUpdate(getQuery("elect_leaders.rq"), connection)
    logger.info(s"Removed sameAs links: $changes")
  }

  def moveRelationships(connection: BigdataSailRepositoryConnection): Unit = {
    val outgoingChanges = submitUpdate(getQuery("move-outgoing-relationships-sameas.rq"), connection)
    logger.info(s"sameAs changes outgoing: $outgoingChanges")
    val incomingChanges = submitUpdate(getQuery("move-incoming-relationships-sameas.rq"), connection)
    logger.info(s"sameAs changes incoming: $incomingChanges")
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

  private val logger = LoggerFactory.getLogger(Merge.getClass)

}