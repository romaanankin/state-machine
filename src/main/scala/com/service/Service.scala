package com.service

import com.model._
import com.repository.{EntityRepository, StateMatrixRepository}

trait Service[E,K] {
  def save(entity: E): Option[E]
  def fetch(key: K): Option[E]
}

class EntityService(implicit p: EntityRepository) extends Service[Entity,String] {
  def save(e: Entity): Option[Entity] = p.save(e)

  def fetch(key: String): Option[Entity] = p.fetch(key)
}

class StateMatrixService (implicit p: StateMatrixRepository) extends Service [StateMatrix,String]{
  def save(e: StateMatrix): Option[StateMatrix] = p.save(e)

  def fetch(key: String): Option[StateMatrix] = p.fetch(key)
}