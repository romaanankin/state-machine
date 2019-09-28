package com

import com.kafka.StateMachineKafkaProducer

object Main extends App {
  val producer = new StateMachineKafkaProducer()
  producer.sendToKafka("t","k","main")


}
