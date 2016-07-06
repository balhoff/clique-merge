package org.monarchinitiative.clique

import java.io.File

import org.apache.jena.graph.Triple
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.system.StreamRDF
import org.apache.jena.sparql.core.Quad
import org.backuity.clist._
import com.bigdata.rdf.store.DataLoader
import org.apache.commons.io.FileUtils
import scala.collection.JavaConverters._

object Load extends Command(description = "Load RDF files into Blazegraph.") with Common {

  var data = arg[File](description = "Folder containing RDF files to be loaded.")

  override def run(): Unit = {
    FileUtils.listFiles(data, null, true).asScala.filterNot(_.getName == "catalog-v001.xml").filter(_.isFile)
      .foreach { datafile =>
        val iri = findOntologyIRI(datafile.getAbsolutePath).getOrElse(s"file:${datafile.getName}")
        DataLoader.main(Array("-defaultGraph", iri, properties.getAbsolutePath, datafile.getAbsolutePath))
      }
  }

  /**
   * Tries to efficiently find the ontology IRI triple without loading
   * the whole file.
   */
  private def findOntologyIRI(filepath: String): Option[String] = {

    object streamer extends StreamRDF {
      def start(): Unit = ()
      def quad(quad: Quad): Unit = ()
      def base(base: String): Unit = ()
      def prefix(prefix: String, iri: String): Unit = ()
      def finish(): Unit = ()
      def triple(triple: Triple): Unit = {
        if (triple.getPredicate.getURI == "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" &&
          triple.getObject.getURI == "http://www.w3.org/2002/07/owl#Ontology") {
          throw new FoundTripleException(triple)
        }
      }
    }

    case class FoundTripleException(val triple: Triple) extends RuntimeException

    try {
      RDFDataMgr.parse(streamer, filepath)
      // If an ontology IRI triple is found, it will be thrown out
      // in an exception. Otherwise, return None.
      None
    } catch {
      case FoundTripleException(triple) => Option(triple.getSubject.getURI)
    }

  }

}