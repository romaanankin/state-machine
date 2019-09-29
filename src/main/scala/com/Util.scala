package com

import com.model.{Entity, State, StateMatrix}
import spray.json.DefaultJsonProtocol.{jsonFormat1, _}
import spray.json.RootJsonFormat

object Util {
  implicit val stateFormat: RootJsonFormat[State] = jsonFormat1(State)
  implicit val entityFormat: RootJsonFormat[Entity] = jsonFormat4(Entity)
  implicit val stateMatrixFormat: RootJsonFormat[StateMatrix] = jsonFormat2(StateMatrix)
}
