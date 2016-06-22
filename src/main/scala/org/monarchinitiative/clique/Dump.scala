package org.monarchinitiative.clique

import java.io.File

import org.backuity.clist._
import com.bigdata.rdf.sail.BigdataSail
import com.bigdata.rdf.sail.BigdataSailRepository
import java.util.Properties
import java.io.FileReader
import java.io.FileOutputStream
import org.openrdf.query.QueryLanguage
import java.io.BufferedOutputStream
import org.openrdf.rio.turtle.TurtleWriter

object Dump extends Command(description = "Dump Blazegraph database to a Turtle RDF file.") with Common {

  var file = arg[File](description = "File name for RDF output.")

  override def run(): Unit = {
    val blazegraphProperties = new Properties()
    blazegraphProperties.load(new FileReader(properties))
    val sail = new BigdataSail(blazegraphProperties)
    val repository = new BigdataSailRepository(sail)
    repository.initialize()
    val blazegraph = repository.getUnisolatedConnection
    val triplesQuery = blazegraph.prepareGraphQuery(QueryLanguage.SPARQL, """
    CONSTRUCT { ?s ?p ?o . }
    WHERE { ?s ?p ?o . }
    """)
    val triplesOutput = new BufferedOutputStream(new FileOutputStream(file))
    triplesQuery.evaluate(new TurtleWriter(triplesOutput))
    triplesOutput.close()
    blazegraph.close()
  }

}