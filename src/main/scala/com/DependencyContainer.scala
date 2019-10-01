package com

import com.controler.Controller
import com.kafka.{StateMachineKafkaProducer, StateStreamProcessor}
import com.repository.{EntityRepository, HistoryRepository, StateMatrixRepository}
import com.service.{EntityService, HistoryService, StateMatrixService}

object DependencyContainer {
  def startApp() {
    implicit val config: Config = new Config
    implicit val stream: StateStreamProcessor = new StateStreamProcessor()
    implicit val producer: StateMachineKafkaProducer = new StateMachineKafkaProducer()
    implicit val entityRepository: EntityRepository = new EntityRepository()
    implicit val entityService: EntityService = new EntityService()
    implicit val stateMatrixRepository: StateMatrixRepository = new StateMatrixRepository()
    implicit val stateMatrixService: StateMatrixService = new StateMatrixService()
    implicit val historyRepository: HistoryRepository = new HistoryRepository()
    implicit val historyService: HistoryService = new HistoryService()
    val controller = new Controller()
    Util.initTopcics
    stream.init()
    controller.init()
  }
}
