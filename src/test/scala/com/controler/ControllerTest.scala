package com.controler

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.model.{Entity, State}
import com.service.{EntityService, HistoryService, StateMatrixService}
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar

class ControllerTest extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar   {

  implicit val mockEntityService: EntityService = mock[EntityService]
  implicit val mockMatrix: StateMatrixService = mock[StateMatrixService]
  implicit val history: HistoryService = mock[HistoryService]

 val controller: Controller = new Controller()
  val route: Route = controller.route
  implicit def rejectionHandler: RejectionHandler = controller.myRejectionHandler



  "The service" should {
    "return a greeting for GET requests to the root path" in {
      Get() ~> route ~> check {
        responseAs[String] shouldEqual "Greatings!"
      }
    }

    "return a NotFound status error for GET requests to the root path and error message in body" in {

      Get("/incorrect-route") ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.NotFound
        responseAs[String] shouldEqual "{\n\t\"error\": \"Not found\"\n}"
      }
    }

  }
}
