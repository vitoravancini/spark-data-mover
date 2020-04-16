import sbt.ExclusionRule

name := "spark-datamover"

version := "0.1"

scalaVersion := "2.11.8"
resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"

val excludeJacksonBinding = ExclusionRule(organization = "org.fasterxml")


libraryDependencies += "com.lihaoyi" %% "utest" % "0.6.8" % "test"
testFrameworks += new TestFramework("utest.runner.Framework")

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0" // % "provided"
libraryDependencies += "mrpowers" % "spark-daria" % "0.35.0-s_2.11"

libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % "7.2.2.jre8"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"
libraryDependencies += "org.firebirdsql.jdbc" % "jaybird" % "2.1.6"
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.17"
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "2.7.3" exclude("javax.servlet",     "servlet-api") exclude("javax.servlet.jsp", "jsp-api") exclude("org.mortbay.jetty", "servlet-api")
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.0-RC2"

// for spark ui work properly
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7"

enablePlugins(sbtdocker.DockerPlugin)

mainClass in assembly := Some("datamover.Cli")

imageNames in docker := Seq(
  // Sets a name with a tag that contains the project version
  ImageName(
    namespace = Some("techindicium"),
    repository = name.value,
    tag = Some("v" + version.value)
  )
)
dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("openjdk:8-jre")
    add(artifact, artifactTargetPath)
    entryPointShell("java", "$JAVA_OPTS", "-jar", artifactTargetPath, "$0", "$@")
  }
}


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) =>
    xs map {
      _.toLowerCase
    } match {
      case "manifest.mf" :: Nil | "index.list" :: Nil | "dependencies" :: Nil =>
        MergeStrategy.discard
      case ps@x :: xs if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case "spring.schemas" :: Nil | "spring.handlers" :: Nil =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case PathList("lib", "static", "Windows", xs@_*) => MergeStrategy.discard
  case PathList("lib", "static", "Mac OS X", xs@_*) => MergeStrategy.discard
  case PathList("com", "amazon", xs@_*) => MergeStrategy.first
  case PathList("com", "amazonaws", xs@_*) => MergeStrategy.first
  case PathList("com", "codahale", xs@_*) => MergeStrategy.first
  case PathList("com", "esotericsoftware", xs@_*) => MergeStrategy.first
  case PathList("com", "google", xs@_*) => MergeStrategy.first
  case PathList("com", "sun", xs@_*) => MergeStrategy.first
  case PathList("com", "yammer", xs@_*) => MergeStrategy.first
  case PathList("commons-beanutils", xs@_*) => MergeStrategy.first
  case PathList("io", "netty", xs@_*) => MergeStrategy.first
  case PathList("javax", "activation", xs@_*) => MergeStrategy.first
  case PathList("javax", "inject", xs@_*) => MergeStrategy.first
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList("javax", "ws", xs@_*) => MergeStrategy.first
  case PathList("net", "java", xs@_*) => MergeStrategy.first
  case PathList("org", "aopalliance", xs@_*) => MergeStrategy.first
  case PathList("org", "apache", xs@_*) => MergeStrategy.first
  case PathList("org", "codehaus", xs@_*) => MergeStrategy.first
  case PathList("org", "glassfish", xs@_*) => MergeStrategy.first
  case PathList("org", "slf4j", xs@_*) => MergeStrategy.first
  case PathList("org", "aopalliance", xs@_*) => MergeStrategy.first
  case PathList("org", "apache", "commons", xs@_*) => MergeStrategy.last
  case PathList("org", "apache", "arrow", xs@_*) => MergeStrategy.last

  case _ => MergeStrategy.first

}

//assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-_${version.value}.jar"

