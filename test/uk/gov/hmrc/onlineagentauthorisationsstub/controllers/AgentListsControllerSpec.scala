/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.onlineagentauthorisationsstub.controllers

import org.apache.pekko.actor.ActorSystem
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import scala.concurrent.Future

class AgentListsControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  implicit val system: ActorSystem = app.actorSystem

  private lazy val controller: AgentListsController = app.injector.instanceOf[AgentListsController]

  private def countAgentLists(body: JsValue): Future[Result] =
    controller.countAgentLists(
      FakeRequest("POST", "/count-agent-lists")
        .withHeaders("Content-Type" -> "application/json")
        .withBody(body)
    )

  ".countAgentLists" should {

    "return 200 and the agent's counts when there is a file for the agent code" in {
      val result = countAgentLists(Json.obj("agentCode" -> "AGENTCODE1"))

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("application/json")
      contentAsJson(result) shouldBe Json.obj("unsubmitted" -> 2, "submitted" -> 3)
    }

    "return 200 and the default counts when there is no file for the agent code" in {
      val result = countAgentLists(Json.obj("agentCode" -> "123456789"))

      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.obj("unsubmitted" -> 0, "submitted" -> 0)
    }

    "return the default counts, not a file lookup, when the agent code is not alphanumeric" in {
      val result = countAgentLists(Json.obj("agentCode" -> "../AGENTCODE1"))

      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.obj("unsubmitted" -> 0, "submitted" -> 0)
    }

    "return 500 when the agent code is INTERNALSERVERERROR" in {
      val result = countAgentLists(Json.obj("agentCode" -> AgentListsController.ErrorAgentCode))

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "message").as[String] should include("Stubbed")
    }

    "return 400 when the agent code is missing" in {
      val result = countAgentLists(Json.obj("somethingElse" -> "AGENTCODE1"))

      status(result) shouldBe Status.BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] should include("Invalid payload")
    }

    "return 400 when the payload is not an object" in {
      val result = countAgentLists(Json.toJson(""))

      status(result) shouldBe Status.BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] should include("Invalid payload")
    }
  }
}
