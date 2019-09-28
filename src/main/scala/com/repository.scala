package com

import com.kafka.StateStreamProcessor

class repository {
  trait Repository[E] {
    def create(smth: Any): E = ???
    def read(key: Any): E = ???
  }

//  class EntityRepository  {
////    val entityStateStore = StateStreamProcessor.entityStateStore
////    def read(key: String): Entity = {
////      val result: String = entityStateStore.get(key)
//////      Entity(key,result)
//    }

//  }

//  class StateMatrixRepository  {
////    override def create: StateMatrix = super.create
////    override def read: StateMatrix = super.read
//  }
}
