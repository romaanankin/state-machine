package com.controler

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.service.{EntityService, StateMatrixService}
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar

class ControllerTest extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar  {

  implicit val mockEntityService: EntityService = mock[EntityService]
  implicit val mockMatrix: StateMatrixService = mock[StateMatrixService]

//  val controller: Controller = mock[Controller]
//  val route = controller.route

}
