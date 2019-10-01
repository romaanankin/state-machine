name := "state-machine"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.3.0",
  "com.typesafe" % "config" % "1.2.1",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.2.0",
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % "2.5.23",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.slf4j" % "slf4j-simple" % "1.8.0-alpha2",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.10",
  "org.mockito" % "mockito-all" % "1.9.5" % Test,
  "net.manub" %% "scalatest-embedded-kafka-streams" % "2.0.0" % "test",
  "com.madewithtea" %% "mockedstreams" % "3.4.0" % "test",
  "org.apache.kafka" % "kafka-streams-test-utils" % "1.1.0" % Test,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)

scalacOptions += "-deprecation"
test in assembly := {}
assemblyJarName in assembly := s"app-assembly.jar"
assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.first
}