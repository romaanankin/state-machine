package com.kafka

import java.util.Properties

import com.Config
import com.Util._
import com.model._
import com.typesafe.scalalogging.Logger
import org.apache.kafka.streams.errors.InvalidStateStoreException
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable, Materialized}
import org.apache.kafka.streams.state.{QueryableStoreType, QueryableStoreTypes, ReadOnlyKeyValueStore}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import spray.json._

import scala.util.{Failure, Success, Try}

class StateStreamProcessor(implicit config: Config)  {
  import org.apache.kafka.streams.scala.Serdes._
  private val logger = Logger(classOf[StateStreamProcessor])

  protected val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, config.appId)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrap)
    p.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, classOf[KafkaExceptionHandler])
    p
  }

  val builder: StreamsBuilder = new StreamsBuilder

  val entityStream: KStream[String, String] = builder.stream[String, String](config.inputEntityTopic)
  entityStream
    .groupByKey
    .reduce((_, v2) => v2)
    .filter((_, _) => true, Materialized.as(config.entityStateStore))

  val stateMatrixStream: KTable[String, String] = builder.table[String, String](config.inputStateTopic)
  stateMatrixStream
    .filter((_, _) => true, Materialized.as(config.stateMatrixStateStore))

  entityStream.map((_, v) => {
    val transition = toTransition(v)
    (transition._1, transition._2)
  }).groupByKey
    .reduce((_, v2) => v2)(Materialized.as(config.transitionHistoryStateStore))
    .toStream
    .to(config.transitionHistoryTopic)

  protected val streams: KafkaStreams = new KafkaStreams(builder.build(), props)

  protected def toTransition(v: String): (String, String) = {
    val key = System.currentTimeMillis().toString + "-name-node"
    val entity: Either[Entity, String] =
      Try(v.parseJson.convertTo[Entity]) match {
        case Success(message)     => Left(message)
        case Failure(exception)   => logger.error(exception.getMessage)
                                     Right(v)
      }

    val value = entity match {
      case Left(e)        =>  com.model.Transition(key, e).toJson.toString()
      case Right(string)  =>  string
    }
    (key,value)
  }

  val init: Unit = streams.start()

  @scala.annotation.tailrec
  final def waitUntilStoreIsQueryable(storeName: String,
                                      store: QueryableStoreType[ReadOnlyKeyValueStore[String, String]],
                                      streams: KafkaStreams):ReadOnlyKeyValueStore[String, String] = {
    Try (streams.store(storeName, store)) match {
      case Success(result) =>  result
      case Failure(exception) => logger.error(exception.getMessage)
        Thread.sleep(100)
        waitUntilStoreIsQueryable(storeName, store, streams)
    }
  }


  lazy val entityStateStore: ReadOnlyKeyValueStore[String, String] =
    waitUntilStoreIsQueryable(config.entityStateStore,
    QueryableStoreTypes.keyValueStore[String, String],streams)

  lazy val stateMatrixStateStore: ReadOnlyKeyValueStore[String, String] =
    waitUntilStoreIsQueryable(config.stateMatrixStateStore,
    QueryableStoreTypes.keyValueStore[String, String],streams)

  lazy val transitionHistoryStateStore: ReadOnlyKeyValueStore[String, String] =
    waitUntilStoreIsQueryable(config.transitionHistoryStateStore,
    QueryableStoreTypes.keyValueStore[String, String],streams)

}
