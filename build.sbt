name := "state-machine"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.3.0",
  "com.typesafe" % "config" % "1.2.1",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.2.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.8",
  "org.scalatest" %% "scalatest" % "3.0.8"
)