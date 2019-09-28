package com.controler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.io.StdIn
import scala.concurrent.{ExecutionContextExecutor, Future}

object Controller {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  //state store
  var entities: List[Entity] = Nil

  final case class Entity(name: String, id: Long)
  final case class State(state: String)

  implicit val itemFormat: RootJsonFormat[Entity] = jsonFormat2(Entity)

  // (fake) state store database query api
  def fetchItem(itemId: Long): Future[Option[Entity]] = Future {
    entities.find(o => o.id == itemId)
  }

  def saveEntity(entity: Entity): Future[Done] = {
    entities = entity match {
        //save to ss logic
      case Entity(name,id) => List(entity) ::: entities
      case _               => entities
    }
    Future { Done }
  }

  def main(args: Array[String]) {

    val route: Route =
      concat(
        get {
          pathPrefix("entity" / LongNumber) { id =>
            // there might be no item for a given id
            val maybeItem: Future[Option[Entity]] = fetchItem(id)

            onSuccess(maybeItem) {
              case Some(item) => complete(item)
              case None       => complete(StatusCodes.NotFound)
            }
          }
        },
        post {
          path("entity-create") {
            entity(as[Entity]) { entity =>
              val saved: Future[Done] = saveEntity(entity)
              onComplete(saved) { done =>
                complete(entity)
              }
            }
          }
        }
      )

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}