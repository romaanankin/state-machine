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
  "org.scalatest" %% "scalatest" % "3.0.8"
)