package com.repository

import com.Config
import com.Util._
import com.kafka.{StateMachineKafkaProducer, StateStreamProcessor}
import com.model._
import spray.json._

trait Repository[E,K] {
  def save(entity: E): E
  def fetch(key: K): E
}

class EntityRepository(implicit kafkaProducer: StateMachineKafkaProducer,
                       store: StateStreamProcessor,config: Config) extends Repository[Entity,String] {

  override def save(entity: Entity): Entity = {
    kafkaProducer.sendToKafka(entity.id, entity.toJson.toString(),
      config.inputEntityTopic)
    entity
  }

  override def fetch(key: String): Entity = {
    store.entityStateStore.get(key).parseJson.convertTo[Entity]
  }
}

class StateMatrixRepository(implicit kafkaProducer: StateMachineKafkaProducer,
                       store: StateStreamProcessor,config: Config) extends Repository[StateMatrix,String] {

  override def save(entity: StateMatrix): StateMatrix = {
    val key = entity.state.state
    val value = entity.toJson.toString()
    kafkaProducer.sendToKafka(key, value,
      config.inputStateTopic)
    entity
  }

  override def fetch(key: String): StateMatrix = {
    store.stateMatrixStateStore
      .get(key)
      .parseJson.convertTo[StateMatrix]
  }
}
