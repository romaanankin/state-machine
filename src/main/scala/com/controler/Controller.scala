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
import com.service.{EntityService, StateMatrixService}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

class Controller(implicit entityService: EntityService, stateMatrixService: StateMatrixService) {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  final case class EntityDAO(name: String)
  implicit val entityDAOFormat: RootJsonFormat[EntityDAO] = jsonFormat1(EntityDAO)

  implicit def myRejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handleNotFound {
        complete((NotFound, "{\n\t\"error\": \"Not found\"\n}"))
      }
      .result()

  def fetchEntity(itemId: String): Future[Option[Entity]] = Future {
    entityService.fetch(itemId)
  }

  def saveEntity(entity: Entity): Future[Option[Entity]] = Future {
    entityService.save(entity)
  }

  def saveStateMatrix(state: StateMatrix): Future[Option[StateMatrix]] = Future {
    stateMatrixService.save(state)
  }

  def fetchStateMatrix(itemId: String): Future[Option[StateMatrix]] = Future {
    stateMatrixService.fetch(itemId)
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
            val UUID = System.currentTimeMillis + "-node-name"
            val initialState = State("init")
            val pendingState = State("pending")

            entity(as[EntityDAO]) { entityDAO =>
              val entityToSave = Entity(UUID, entityDAO.name, initialState,pendingState)
              val saved: Future[Option[Entity]] = saveEntity(entityToSave)
              onSuccess(saved) {
                case Some(entity) => complete(entity)
                case None         =>
                  complete(StatusCodes.InternalServerError,"{\n\t\"error\": \"Server ERROR. Not saved\"\n}")
              }
            }
          }
        },
        post {
          path("state-matrix") {
            entity(as[StateMatrix]) { stateMatrix =>
              val saved: Future[Option[StateMatrix]] = saveStateMatrix(stateMatrix)
              onSuccess(saved) {
                case Some(state) => complete(state)
                case None              =>
                  complete(StatusCodes.InternalServerError,"{\n\t\"error\": \"Server ERROR. Not saved\"\n}")

              }
            }
          }
        },
        get {
          pathPrefix("state-matrix" / Remaining) { id =>
            val maybeMatrix: Future[Option[StateMatrix]] = fetchStateMatrix(id)

            onSuccess(maybeMatrix) {
              case Some(item) => complete(item)
              case None       => complete(StatusCodes.NotFound,"{\n\t\"error\": \"Not found\"\n}")
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