package com.controler

import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.model.Entity
import com.service.{EntityService, HistoryService, StateMatrixService}
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar

class ControllerTest extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar  {

  implicit val mockEntityService: EntityService = mock[EntityService]
  implicit val mockMatrix: StateMatrixService = mock[StateMatrixService]
  implicit val history: HistoryService = mock[HistoryService]

  class C extends Controller

  val route: Route = new C().route

  "The service" should {

    "return a greeting for GET requests to the root path" in {
      val id = "some"
      Get( s"/entity/$id") ~> route ~> check {

        handled shouldBe false
      }
    }
  }

}
