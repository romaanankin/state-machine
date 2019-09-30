package com.controler

import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import com.service.{EntityService, StateMatrixService}
import org.scalatestplus.mockito.MockitoSugar

class ControllerTest extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar  {

  implicit val mockEntityService: EntityService = mock[EntityService]
  implicit val mockMatrix: StateMatrixService = mock[StateMatrixService]

  val controller: Controller = mock[Controller]
  val route = controller.route

  "The service" should {

    "return a greeting for GET requests to the root path" in {
      // tests:
      Get("state-matrix") ~> route ~> check {
        responseAs[String] shouldEqual "{\n\t\"error\": \"Not found\"\n}"
      }
    }
  }
}
