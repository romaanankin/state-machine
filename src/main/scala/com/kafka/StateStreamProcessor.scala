package com.kafka

import java.io
import java.util.Properties

import com.Config
import com.Util._
import com.model._
import com.typesafe.scalalogging.Logger
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable, Materialized}
import org.apache.kafka.streams.state.{QueryableStoreTypes, ReadOnlyKeyValueStore}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import spray.json._

class StateStreamProcessor(implicit config: Config)  {
  import org.apache.kafka.streams.scala.Serdes._
  private val logger = Logger(classOf[StateStreamProcessor])

  var entityStateStore: ReadOnlyKeyValueStore[String, String] = _
  var stateMatrixStateStore: ReadOnlyKeyValueStore[String, String] = _
  var transitionHistoryStateStore: ReadOnlyKeyValueStore[String, String] = _

  protected val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, config.appId)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrap)
    p.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, classOf[KafkaExceptionHandler])
    p
  }

  private val builder: StreamsBuilder = new StreamsBuilder

  protected val entityStream: KStream[String, String] = builder.stream[String, String](config.inputEntityTopic)
  entityStream
    .groupByKey
    .reduce((_, v2) => v2)
    .filter((_, _) => true, Materialized.as(config.entityStateStore))

  protected val stateMatrixStream: KTable[String, String] = builder.table[String, String](config.inputStateTopic)
  stateMatrixStream
    .filter((_, _) => true, Materialized.as(config.stateMatrixStateStore))

  entityStream.map((_, v) => {
    val transition = toTransition(v)
    (transition._1, transition._2)
  }).groupByKey
    .reduce((v1, v2) => v2)(Materialized.as(config.transitionHistoryStateStore))
    .toStream
    .to(config.transitionHistoryTopic)

  protected val streams: KafkaStreams = new KafkaStreams(builder.build(), props)

  protected def toTransition(v: String): (String, String) = {
    val key = System.currentTimeMillis().toString + "-name-node"
    val entity: Either[Entity, String] =

      try {
        Left(v.parseJson.convertTo[Entity])
      } catch {
        case e: Exception => logger.error(s"Message -- $v -- can't be serialized into Entity" + e.getMessage)
          Right(v)
      }

    val value = entity match {
      case Left(e)        =>  com.model.Transition(key, e).toJson.toString()
      case Right(string)  =>  string
    }
    (key,value)
  }

  def init(): Unit = {
    streams.start()
    Thread.sleep(10000)
    entityStateStore = streams.store(config.entityStateStore, QueryableStoreTypes.keyValueStore[String, String]())
    stateMatrixStateStore = streams.store(config.stateMatrixStateStore, QueryableStoreTypes.keyValueStore[String, String]())
    transitionHistoryStateStore = streams.store(config.transitionHistoryStateStore,QueryableStoreTypes.keyValueStore[String, String]())
  }
}
