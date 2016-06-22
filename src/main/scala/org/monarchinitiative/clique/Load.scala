package org.monarchinitiative.clique

import java.io.File

import org.backuity.clist._

import com.bigdata.rdf.store.DataLoader

object Load extends Command(description = "Load RDF files into Blazegraph.") with Common {

  var data = arg[File](description = "Folder containing RDF files to be loaded.")

  override def run(): Unit = DataLoader.main(Array(properties.getAbsolutePath, data.getAbsolutePath))

}