package com.service

import com.model._
import com.repository.{EntityRepository, StateMatrixRepository}

trait Service[E,K] {
  def save(entity: E): E
  def fetch(key: K): E
}

class EntityService(p: EntityRepository) extends Service[Entity,String] {
  def save(e: Entity): Entity = p.save(e)

  def fetch(key: String): Entity = p.fetch(key)
}

class StateMatrixService (p: StateMatrixRepository) extends Service [StateMatrix,String]{
  def save(e: StateMatrix): StateMatrix = p.save(e)

  def fetch(key: String): StateMatrix = p.fetch(key)
}