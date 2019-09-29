package com.repository

import com.Config
import com.Util._
import com.kafka.{StateMachineKafkaProducer, StateStreamProcessor}
import com.model._
import spray.json._

trait Repository[E,K] {
  def save(entity: E): Option[E]
  def fetch(key: K): Option[E]
}

class EntityRepository(implicit kafkaProducer: StateMachineKafkaProducer,
                       store: StateStreamProcessor,config: Config) extends Repository[Entity,String] {

  override def save(entity: Entity): Option[Entity] = {
     try {
       kafkaProducer.sendToKafka(entity.entity_id, entity.toJson.toString(),
         config.inputEntityTopic)
       Some(entity)
     } catch {
       case e: Exception => None
    }
  }

  override def fetch(key: String): Option[Entity] = {
    try {
      Some(store.entityStateStore.get(key)
        .parseJson.convertTo[Entity])
    } catch {
      case e: Exception => None
    }
  }
}

class StateMatrixRepository(implicit kafkaProducer: StateMachineKafkaProducer,
                       store: StateStreamProcessor,config: Config) extends Repository[StateMatrix,String] {

  override def save(entity: StateMatrix): Option[StateMatrix] = {
    val key = entity.state.state
    val value = entity.toJson.toString()

    try {
      kafkaProducer.sendToKafka(key, value,
        config.inputStateTopic)
      Some(entity)
    } catch {
      case e: Exception => None
    }
  }

  override def fetch(key: String): Option[StateMatrix] = {
    try {
      Some(store.stateMatrixStateStore
        .get(key)
        .parseJson.convertTo[StateMatrix])
    } catch {
      case e: Exception => None
    }
  }
}
