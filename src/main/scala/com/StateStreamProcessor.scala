package com

import java.util.Properties

import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable, Materialized}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.state.{QueryableStoreTypes, ReadOnlyKeyValueStore}

object StateStreamProcessor extends App {
import org.apache.kafka.streams.scala.Serdes._

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-stream")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, classOf[KafkaExceptionHandler])
    p
  }

  val builder: StreamsBuilder = new StreamsBuilder
  val testStream: KTable[String, String] = builder.table[String, String]("entity-input-topic")
  testStream.filter((_,_) => true, Materialized.as("entity-store"))

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()

  Thread.sleep(10000)
    val keyValueStore: ReadOnlyKeyValueStore[String, String] =
      streams.store("counts-store", QueryableStoreTypes.keyValueStore[String,String]())
   keyValueStore.all().forEachRemaining(println)
    println(streams.state())
}
