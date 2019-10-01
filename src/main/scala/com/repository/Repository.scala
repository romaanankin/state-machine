package com.repository

import com.Config
import com.Util._
import com.kafka.{StateMachineKafkaProducer, StateStreamProcessor}
import com.model._
import com.typesafe.scalalogging.Logger
import org.apache.kafka.streams.state.KeyValueIterator
import spray.json._

import scala.util.{Failure, Success, Try}

trait Repository[E,K] {
  val logger = Logger(classOf[Repository[_,_]])
  def save(entity: E): Option[E]
  def fetch(key: K): Option[E]
}

class EntityRepository(implicit kafkaProducer: StateMachineKafkaProducer,
                       store: StateStreamProcessor,config: Config) extends Repository[Entity,String] {

  override def save(entity: Entity): Option[Entity] = {
    Try( kafkaProducer.sendToKafka(entity.entity_id, entity.toJson.toString(),
      config.inputEntityTopic)) match {
      case Success(value)     =>  Some(entity)
      case Failure(exception) =>  logger.error(exception.getMessage)
                                   None
    }
  }

  override def fetch(key: String): Option[Entity] = {
    Try {
      store.entityStateStore.get(key)
        .parseJson.convertTo[Entity]
    } match {
      case Success(entity)    => Some(entity)
      case Failure(exception) => logger.error(exception.getMessage)
                                 None
    }
  }
}

class StateMatrixRepository(implicit kafkaProducer: StateMachineKafkaProducer,
                       store: StateStreamProcessor,config: Config) extends Repository[StateMatrix,String] {

  override def save(entity: StateMatrix): Option[StateMatrix] = {
    Try {
      val key = entity.state.state
      val value = entity.toJson.toString()
      kafkaProducer.sendToKafka(key, value, config.inputStateTopic)
    } match {
      case Success(_)         => Some(entity)
      case Failure(exception) => logger.error(exception.getMessage)
                                 None
    }
  }

  override def fetch(key: String): Option[StateMatrix] = {

    Try {
      store.stateMatrixStateStore.get(key)
        .parseJson.convertTo[StateMatrix]
    } match {
      case Success(stateMatrix) => Some(stateMatrix)
      case Failure(exception)   => logger.error(exception.getMessage)
                                    None
    }
  }
}

class HistoryRepository (implicit kafkaProducer: StateMachineKafkaProducer,
                                   store: StateStreamProcessor,config: Config) {

  private val logger: Logger = Logger(classOf[HistoryRepository])

  def fetchAll(): Option[List[String]] = {
    val buffer = scala.collection.mutable.ListBuffer.empty[String]
    Try {
      val s: KeyValueIterator[String, String] = store.transitionHistoryStateStore.all()
      while (s.hasNext) {
        val n = s.next().value.toString
        buffer += n
      }
      buffer.foreach(hm => logger.info(hm))
      buffer.toList
    } match {
      case Success(result)    => Some(result)
      case Failure(exception) => logger.error(exception.getMessage)
                                 None
    }
  }

}
