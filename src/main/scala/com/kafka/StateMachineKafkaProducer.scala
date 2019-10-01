package com.kafka

import java.util.Properties

import com.Config
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.util.{Failure, Success, Try}

class StateMachineKafkaProducer(implicit config: Config) {
  private val logger = Logger(classOf[StateMachineKafkaProducer])

  protected val props: Properties = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrap)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.stringSerializer)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config.stringSerializer)

  protected val producer = new KafkaProducer[String, String](props)

  def sendToKafka(key: String, value: String, topic: String): Unit = {
      val record = new ProducerRecord[String, String](topic, key, value)

      Try(producer.send(record)) match {
        case Success(metadata)  => logger.info(s"Kafka producer sent record(key=%s value=%s) " +
          "meta(partition=%d, offset=%d)\n",
          record.key(), record.value(),
          metadata.get().partition(),
          metadata.get().offset())
        case Failure(exception) => logger.error(exception.getMessage)
      }
  }
}
