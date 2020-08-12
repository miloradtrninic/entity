name := "entity"

version := "0.1"

scalaVersion := "2.11.12"

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

enablePlugins(JavaServerAppPackaging)
enablePlugins(JavaAppPackaging)


libraryDependencies ++= {
  val sparkVer = "2.4.0"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer % "provided",
    "org.apache.spark" %% "spark-mllib" % sparkVer % "provided"
  )
}

//conflictManager := ConflictManager.strict

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x if x.startsWith("NativeLibrary") => MergeStrategy.last
  case x if x.startsWith("aws") => MergeStrategy.last
  case _ => MergeStrategy.last
}
////
assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {
    j => {
      j.data.getName.startsWith("spark-nlp")
    }
  }
}
