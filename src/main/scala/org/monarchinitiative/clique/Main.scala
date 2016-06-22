package org.monarchinitiative.clique

import org.backuity.clist._

object Main extends App {
  
  Cli.parse(args).withProgramName("clique-merge").withCommands(Load, Dump, Merge).foreach(_.run())

}