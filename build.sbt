name := "entity"

version := "0.1"

scalaVersion := "2.11.12"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

enablePlugins(JavaServerAppPackaging)
enablePlugins(JavaAppPackaging)


libraryDependencies ++= {
  val sparkVer = "2.4.5"
  val sparkNLP = "2.4.3"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer,
    "org.apache.spark" %% "spark-sql" % sparkVer,
    "org.apache.spark" %% "spark-streaming" % sparkVer,
    "org.apache.spark" %% "spark-mllib" %sparkVer,
    "org.apache.spark" %% "spark-hive" % sparkVer,
    "org.apache.spark" %% "spark-graphx" % sparkVer,
    "org.apache.spark" %% "spark-yarn" % sparkVer,
    "com.johnsnowlabs.nlp" %% "spark-nlp" % sparkNLP
  )
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {
    j => {
      j.data.getName.startsWith("spark-core") ||
        j.data.getName.startsWith("spark-sql") ||
        j.data.getName.startsWith("spark-hive") ||
        j.data.getName.startsWith("spark-mllib") ||
        j.data.getName.startsWith("spark-graphx") ||
        j.data.getName.startsWith("spark-yarn") ||
        j.data.getName.startsWith("spark-streaming") ||
        j.data.getName.startsWith("hadoop")
    }
  }
}
