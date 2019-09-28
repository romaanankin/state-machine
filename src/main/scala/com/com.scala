import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

package object com {
  final case class Entity(id: String,name: String,state: State)
  final case class State(state: String)
  final case class StateMatrix(state: State,transitions: List[String])

  object Config {
    val inputEntityTopic: String = ConfigFactory.load().getString("kafka.topics.input-entity-topic.value")
    val inputStateTopic: String = ConfigFactory.load().getString("kafka.topics.input-state-topic.value")
    val bootstrap: String = ConfigFactory.load().getString("kafka.bootstrap.value")
    val stringSerializer: String = ConfigFactory.load().getString("kafka.serializers.string-serializer.value")
  }
}
