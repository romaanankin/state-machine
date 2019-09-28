package com.kafka

import java.util

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.RecordTooLargeException
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.apache.kafka.streams.errors.ProductionExceptionHandler.ProductionExceptionHandlerResponse

class KafkaExceptionHandler extends ProductionExceptionHandler {
  override def handle(record: ProducerRecord[Array[Byte], Array[Byte]], exception: Exception):
  ProductionExceptionHandler.ProductionExceptionHandlerResponse =
  exception match {
    case e: RecordTooLargeException =>
      print(s"There was a Deserialization issue with the message due to its size $record" +
        record.timestamp() + record.headers() + record.partition() + "record topic: " + record.topic())
      print(e.recordTooLargePartitions())
      ProductionExceptionHandlerResponse.CONTINUE
    case _ =>
      print(s"There was a Deserialization issue with the message $record" +
        record.timestamp() + record.headers() + record.partition() + "record topic: " + record.topic())
      ProductionExceptionHandlerResponse.CONTINUE
  }

  override def configure(configs: util.Map[String, _]): Unit = {}
}
