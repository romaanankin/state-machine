package com

import com.controler.Controller
import com.kafka.{StateMachineKafkaProducer, StateStreamProcessor}
import com.repository.{EntityRepository, Repository, StateMatrixRepository}
import com.service.{EntityService, StateMatrixService}

object Main extends App {
  implicit val config: Config = new Config
  implicit val stream: StateStreamProcessor = new StateStreamProcessor()
  stream.init()

  implicit val producer: StateMachineKafkaProducer = new StateMachineKafkaProducer()
  implicit val entityRepository: EntityRepository = new EntityRepository()
  implicit val entityService: EntityService = new EntityService()
  implicit val stateMatrixRepository: StateMatrixRepository = new StateMatrixRepository()
  implicit val stateMatrixService: StateMatrixService = new StateMatrixService()

  stream.entityStateStore.all().forEachRemaining(println)

  //launch last
  new Controller().init()

}
