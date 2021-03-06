enablePlugins(JavaAppPackaging)

organization  := "org.monarchinitiative"

name          := "clique-merge"

version       := "0.0.3"

scalaVersion  := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

mainClass in Compile := Some("org.monarchinitiative.clique.Main")

resolvers += Resolver.mavenLocal

javaOptions += "-Xmx12G"

libraryDependencies ++= {
    Seq(
      "com.blazegraph"     %  "bigdata-core"     % "2.1.2",
      "org.backuity.clist" %% "clist-core"       % "2.0.2",
      "org.backuity.clist" %% "clist-macros"     % "2.0.2" % "provided",
      "org.apache.jena"    %  "apache-jena-libs" % "3.1.0",
      "commons-io"         %  "commons-io"       % "2.4"
    )
}
