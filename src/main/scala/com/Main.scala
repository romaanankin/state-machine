package com

object Main extends App {
  val producer = new StateMachineKafkaProducer()
  producer.sendToKafka("t","k","main")
}
