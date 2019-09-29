package com.model

final case class Entity(id: String,name: String,state: State)
final case class State(state: String)
final case class StateMatrix(state: State,transitions: List[String])