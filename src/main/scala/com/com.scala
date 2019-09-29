import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

package object com {

  class Config {
    val appId: String = ConfigFactory.load().getString("kafka.stream.appId.value")
    val entityStateStore: String = ConfigFactory.load().getString("kafka.topics.entity-state-store.value")
    val stateMatrixStateStore: String = ConfigFactory.load().getString("kafka.topics.state-matrix-store.value")
    val inputEntityTopic: String = ConfigFactory.load().getString("kafka.topics.entity-input-topic.value")
    val inputStateTopic: String = ConfigFactory.load().getString("kafka.topics.state-matrix-input-topic.value")
    val bootstrap: String = ConfigFactory.load().getString("kafka.bootstrap.value")
    val stringSerializer: String = ConfigFactory.load().getString("kafka.serializers.string-serializer.value")
  }
}
