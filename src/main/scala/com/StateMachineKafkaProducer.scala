package com

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.util.Try

class StateMachineKafkaProducer {
  val props: Properties = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Config.bootstrap)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Config.stringSerializer)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Config.stringSerializer)

  val producer = new KafkaProducer[String, String](props)

  def sendToKafka(key: String, value: String, topic: String) {
    Try {
      val record = new ProducerRecord[String, String](topic, key, value)
      val metadata = producer.send(record)
      printf(s"sent record(key=%s value=%s) " +
        "meta(partition=%d, offset=%d)\n",
        record.key(), record.value(),
        metadata.get().partition(),
        metadata.get().offset())
    }
  }
}
