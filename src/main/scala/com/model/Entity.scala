package com.model

final case class Entity(entity_id: String,name: String,from: State,to: State)
final case class State(state: String)
final case class StateMatrix(state: State,transitions: List[String])
final case class Transition(transition_id: String, entity: Entity)