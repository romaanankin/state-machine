package com.repository

import com.Config
import com.Util._
import com.kafka.{StateMachineKafkaProducer, StateStreamProcessor}
import com.model._
import com.typesafe.scalalogging.Logger
import spray.json._

trait Repository[E,K] {
  val logger = Logger(classOf[Repository[_,_]])
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
       case e: Exception =>  logger.error(e.getMessage)
                             None
       case _            =>  None
    }
  }

  override def fetch(key: String): Option[Entity] = {
    try {
      store.entityStateStore.get(key) match {
        case s: String     => s.parseJson match {
          case j: JsValue  => j.convertTo[Entity] match {
            case e: Entity => Some(e)
          }
        }
      }
    } catch {
      case e: Exception    =>  logger.error(e.getMessage)
                               None
      case _               =>  None
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
      case e: Exception => logger.error(e.getMessage)
                           None
    }
  }

  override def fetch(key: String): Option[StateMatrix] = {
    try {
      store.stateMatrixStateStore.get(key) match {
        case s: String     => s.parseJson match {
          case j: JsValue  => j.convertTo[StateMatrix] match {
            case e: StateMatrix => Some(e)
          }
        }
      }
    } catch {
      case e: Exception    =>  logger.error(e.getMessage)
                               None
      case _               =>  None
    }
  }
}
