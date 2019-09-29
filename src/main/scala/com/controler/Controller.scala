package com.controler

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, Route}
import akka.stream.ActorMaterializer
import com.model._
import com.Util._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

//add service to controller
class Controller {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  //state store
  var entities: List[Entity] = Nil

  final case class EntityDAO(name: String)
//  implicit val stateFormat: RootJsonFormat[State] = jsonFormat1(State)
//  implicit val entityFormat: RootJsonFormat[Entity] = jsonFormat3(Entity)
  implicit val entityDAOFormat: RootJsonFormat[EntityDAO] = jsonFormat1(EntityDAO)

  implicit def myRejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handleNotFound {
        complete((NotFound, "{\n\t\"error\": \"Not found\"\n}"))
      }
      .result()

  // (fake) state store database query api
  def fetchEntity(itemId: String): Future[Option[Entity]] = Future {
    entities.find(o => o.id == itemId)
  }

  def saveEntity(entity: Entity): Future[Done] = {
    entities = entity match {
        //save to ss logic
      case Entity(id,name,initialState) => List(Entity(id,name,initialState)) ::: entities
      case _               => entities
    }
    Future { Done }
  }

  def init() {

    val route: Route =
      concat(
        get {
          pathPrefix("entity" / Remaining) { id =>
            val maybeEntity: Future[Option[Entity]] = fetchEntity(id)

            onSuccess(maybeEntity) {
              case Some(item) => complete(item)
              case None       => complete(StatusCodes.NotFound,"{\n\t\"error\": \"Not found\"\n}")
            }
          }
        },
        post {
          path("entity-create") {
            val UUID = System.currentTimeMillis + "node-name"
            val initialState = State("init")

            entity(as[EntityDAO]) { entityDAO =>
              val entityToSave = Entity(UUID, entityDAO.name, initialState)
              val saved: Future[Done] = saveEntity(entityToSave)
              onComplete(saved) { done =>
                complete(entityToSave)
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