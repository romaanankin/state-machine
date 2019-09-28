import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

package object com {
  object Config {
    val inputEntityTopic: String = ConfigFactory.load().getString("kafka.topics.input-entity-topic.value")
    val inputStateTopic: String = ConfigFactory.load().getString("kafka.topics.input-state-topic.value")
    val bootstrap: String = ConfigFactory.load().getString("kafka.bootstrap.value")
    val stringSerializer: String = ConfigFactory.load().getString("kafka.serializers.string-serializer.value")
  }
}
