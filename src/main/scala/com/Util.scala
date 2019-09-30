package com

import com.kafka.StateMachineKafkaProducer
import com.model._
import spray.json.DefaultJsonProtocol.{jsonFormat1, _}
import spray.json.RootJsonFormat

object Util {
  implicit val stateFormat: RootJsonFormat[State] = jsonFormat1(State)
  implicit val entityFormat: RootJsonFormat[Entity] = jsonFormat4(Entity)
  implicit val stateMatrixFormat: RootJsonFormat[StateMatrix] = jsonFormat2(StateMatrix)
  implicit val transitionFormat: RootJsonFormat[Transition] = jsonFormat2(Transition)
  def initTopcics(implicit producer: StateMachineKafkaProducer, config: Config ): Unit = {
    producer.sendToKafka("entity input topic heardbeat","",config.inputEntityTopic)
    producer.sendToKafka("transition input topic heardbeat","",config.transitionHistoryTopic)
    producer.sendToKafka("state input topic heardbeat","",config.inputStateTopic)
  }
}
