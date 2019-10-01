package com.controler

import java.util
import java.util.Properties

import com.Config
import com.kafka.{KafkaExceptionHandler, StateStreamProcessor}
import net.manub.embeddedkafka.streams.EmbeddedKafkaStreamsAllInOne
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import org.apache.kafka.streams.scala.Serdes
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.apache.kafka.streams.{StreamsConfig, Topology, TopologyTestDriver}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

class StateStreamProcessorTest extends FlatSpec with Matchers with EmbeddedKafkaStreamsAllInOne {
  implicit val config: Config = new Config()
  val p = new Properties()
  p.put(StreamsConfig.APPLICATION_ID_CONFIG, config.appId)
  p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrap)
  p.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, classOf[KafkaExceptionHandler])

  val stringSerializer: Serializer[String] = Serdes.String.serializer()
  val stringDesirializer: Deserializer[String] = Serdes.String.deserializer()
  val streamApp =  new StateStreamProcessor()
  val topology: Topology = streamApp.builder.build()
  val driver = new TopologyTestDriver(topology,p)
  val producerRecordFactory =
    new ConsumerRecordFactory(config.inputEntityTopic,stringSerializer,stringSerializer)

  def listDataToTopic(topic: String): util.List[ConsumerRecord[Array[Byte], Array[Byte]]] = {
    val list: util.List[ConsumerRecord[Array[Byte], Array[Byte]]] = ArrayBuffer(
      producerRecordFactory.create(config.inputEntityTopic, "key", "value2"),
      producerRecordFactory.create(config.inputEntityTopic, "key", "value2"),
      producerRecordFactory.create(config.inputEntityTopic, "key", "value3"),
      producerRecordFactory.create(config.inputEntityTopic, "key1", "value4")
    ).asJava
    list
  }

  it should "Recieve message and store them into state store" in {
    driver.pipeInput(producerRecordFactory.create(config.inputEntityTopic,"key","entity"))
    val entityStateStore: KeyValueStore[String, String] = driver.getKeyValueStore(config.entityStateStore)

    driver.pipeInput(producerRecordFactory.create(config.inputStateTopic,"key","state"))
    val stateSS: KeyValueStore[String, String] = driver.getKeyValueStore(config.stateMatrixStateStore)
    val historySS: KeyValueStore[String, String] = driver.getKeyValueStore(config.transitionHistoryStateStore)

    val historyMessage = driver.readOutput(config.transitionHistoryTopic,stringDesirializer,stringDesirializer)

    entityStateStore.get("key") should be ("entity")
    stateSS.get("key")          should be ("state")
    historySS.get(historyMessage.key())        should be (historyMessage.value())
  }

  it should "Containing only latest arrival value by key" in {
    driver.pipeInput(listDataToTopic(config.inputEntityTopic))
    val entityStateStore: KeyValueStore[String, String] = driver.getKeyValueStore(config.entityStateStore)

    entityStateStore.get("key") should be ("value3")
    entityStateStore.get("key1") should be ("value4")
  }
}
