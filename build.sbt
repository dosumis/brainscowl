organization  := "dosumis"

name          := "brainscowl"

version       := "0.0.1"

scalaVersion  := "2.11.8" // scowl allegedly works with scala 2.12 - but tests failed

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

licenses := Seq("MIT license" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/dosumis/brainscowl"))

javaOptions += "-Xmx6G"

// initialCommands in (Test, console) := """ammonite.Main().run()"""

libraryDependencies ++= {
    //  TO CHECK: Is OWL API really needed or does in come with scowl?
    //   "net.sourceforge.owlapi"     %  "owlapi-distribution" % "4.2.1",

    Seq(
      "org.semanticweb.elk"    %   "elk-owlapi"          % "0.4.1" withJavadoc(),
      "org.phenoscape"             %% "scowl"            % "1.3" withJavadoc(),
      "org.scalactic" %% "scalactic" % "3.0.1",      
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"  withJavadoc()
      )  // Move scowl to 1.3??
}

