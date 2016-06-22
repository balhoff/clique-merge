package org.monarchinitiative.clique

import java.io.FileReader
import java.util.Properties

import org.backuity.clist._
import java.io.File

trait Common extends Command {

  def run(): Unit

  var properties = opt[File](description = "Blazegraph properties file. Will look for a file called blazegraph.properties by default.", default = new File("blazegraph.properties"))

}