package com.kafka

import java.util.Properties

import com.Config
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KTable, Materialized}
import org.apache.kafka.streams.state.{QueryableStoreTypes, ReadOnlyKeyValueStore}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

class StateStreamProcessor(implicit config: Config)  {
  import org.apache.kafka.streams.scala.Serdes._

  var entityStateStore: ReadOnlyKeyValueStore[String, String] = _
  var stateMatrixStateStore: ReadOnlyKeyValueStore[String, String] = _

  protected val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, config.appId)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrap)
    p.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, classOf[KafkaExceptionHandler])
    p
  }

  private val builder: StreamsBuilder = new StreamsBuilder

  //always setup topics first
  protected val entityStream: KTable[String, String] = builder.table[String, String](config.inputEntityTopic)
  entityStream.filter((_, _) => true, Materialized.as(config.entityStateStore))

  protected val stateMatrixStream: KTable[String, String] = builder.table[String, String](config.inputStateTopic)
  stateMatrixStream.filter((_, _) => true, Materialized.as(config.stateMatrixStateStore))

  protected val streams: KafkaStreams = new KafkaStreams(builder.build(), props)

  def init(): Unit = {
    streams.start()
    Thread.sleep(10000)
    entityStateStore = streams.store(config.entityStateStore, QueryableStoreTypes.keyValueStore[String, String]())
    stateMatrixStateStore = streams.store(config.stateMatrixStateStore, QueryableStoreTypes.keyValueStore[String, String]())
  }
}
