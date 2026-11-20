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

package uk.gov.hmrc.onlineagentauthorisationsstub.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.test.Helpers.*

import scala.concurrent.Future

class StubResourceSpec extends AnyWordSpec with Matchers {

  private object TestResource extends StubResource

  private val existingPath = "/resources.agentLists/default/countAgentLists.json"
  private val missingPath  = "/resources.agentLists/missing/countAgentLists.json"

  "resourceExists" should {
    "return true for a file on the classpath" in {
      TestResource.resourceExists(existingPath) shouldBe true
    }

    "return false for a file that is not on the classpath" in {
      TestResource.resourceExists(missingPath) shouldBe false
    }
  }

  "findResource" should {
    "return the file's content" in {
      TestResource.findResource(existingPath).value should include("unsubmitted")
    }

    "return None for a missing file" in {
      TestResource.findResource(missingPath) shouldBe None
    }
  }

  "jsonResourceAsResponse" should {
    "return 200 with the file as JSON" in {
      val result = Future.successful(TestResource.jsonResourceAsResponse(existingPath))

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("application/json")
    }

    "return 404 for a missing file" in {
      status(Future.successful(TestResource.jsonResourceAsResponse(missingPath))) shouldBe Status.NOT_FOUND
    }
  }

  extension [A](option: Option[A]) private def value: A = option.getOrElse(fail("expected Some"))
}
