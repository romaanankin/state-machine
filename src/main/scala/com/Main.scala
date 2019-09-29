package com

import com.controler.Controller
import com.kafka.{StateMachineKafkaProducer, StateStreamProcessor}
import com.repository.Repository

object Main extends App {
  implicit val config: Config = new Config
  implicit val producer: StateMachineKafkaProducer = new StateMachineKafkaProducer()
//    producer.sendToKafka("t","k","entity-input-topic")
  implicit val stream: StateStreamProcessor = new StateStreamProcessor()
  stream.init()
  stream.entityStateStore.all().forEachRemaining(println)



  //launch last
  new Controller().init()

}
