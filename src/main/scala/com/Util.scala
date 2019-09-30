package com

import com.kafka.StateMachineKafkaProducer
import com.model._
import com.typesafe.scalalogging.Logger
import spray.json.DefaultJsonProtocol.{jsonFormat1, _}
import spray.json.RootJsonFormat

object Util {
  val logger = Logger("Util object logger")

  implicit val stateFormat: RootJsonFormat[State] = jsonFormat1(State)
  implicit val entityFormat: RootJsonFormat[Entity] = jsonFormat4(Entity)
  implicit val stateMatrixFormat: RootJsonFormat[StateMatrix] = jsonFormat2(StateMatrix)
  implicit val transitionFormat: RootJsonFormat[Transition] = jsonFormat2(Transition)
  def initTopcics(implicit producer: StateMachineKafkaProducer, config: Config ): Unit = {
    producer.sendToKafka("heardbeat","entity input topic ",config.inputEntityTopic)
    producer.sendToKafka("heardbeat","transition input topic ",config.transitionHistoryTopic)
    producer.sendToKafka("heardbeat","state input topic ",config.inputStateTopic)
    logger.info("Initialize messages with Key --heartbeat-- hear sended to Kafka")
  }
}
