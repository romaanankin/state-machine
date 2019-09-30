package com

import com.controler.Controller
import com.kafka.{StateMachineKafkaProducer, StateStreamProcessor}
import com.repository.{EntityRepository, StateMatrixRepository}
import com.service.{EntityService, StateMatrixService}

object DependencyContainer {
  def startApp() {
    implicit val config: Config = new Config
    implicit val stream: StateStreamProcessor = new StateStreamProcessor()
    implicit val producer: StateMachineKafkaProducer = new StateMachineKafkaProducer()
    implicit val entityRepository: EntityRepository = new EntityRepository()
    implicit val entityService: EntityService = new EntityService()
    implicit val stateMatrixRepository: StateMatrixRepository = new StateMatrixRepository()
    implicit val stateMatrixService: StateMatrixService = new StateMatrixService()
    Util.initTopcics
    stream.init()
//    stream.entityStateStore.all().forEachRemaining(println)
    new Controller().init()
  }
}
