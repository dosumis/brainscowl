organization  := dosumis"

name          := "brainscowl"

version       := "0.0.1"

scalaVersion  := "2.12.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

licenses := Seq("MIT license" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/dosumis/brainscowl"))

javaOptions += "-Xmx6G"

// initialCommands in (Test, console) := """ammonite.Main().run()"""

libraryDependencies ++= {
    //  TO CHECK: Is OWL API really needed or does in come with scowl?
    Seq(
      "net.sourceforge.owlapi"     %  "owlapi-distribution" % "4.2.1",
      "org.semanticweb.elk"    %   "elk-owlapi"          % "0.4.1",
      "org.phenoscape"             %% "scowl"            % "1.1",
      )
}

