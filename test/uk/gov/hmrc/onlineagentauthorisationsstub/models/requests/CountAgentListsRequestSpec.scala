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

package uk.gov.hmrc.onlineagentauthorisationsstub.models.requests

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, Json}

class CountAgentListsRequestSpec extends AnyWordSpec with Matchers {

  "CountAgentListsRequest (JSON)" should {
    "read an agent code" in {
      Json.obj("agentCode" -> "AGENTCODE1").as[CountAgentListsRequest] shouldBe CountAgentListsRequest("AGENTCODE1")
    }

    "write an agent code" in {
      Json.toJson(CountAgentListsRequest("AGENTCODE1")) shouldBe Json.obj("agentCode" -> "AGENTCODE1")
    }

    "fail to read when the agent code is missing" in {
      Json.obj().validate[CountAgentListsRequest] shouldBe a[JsError]
    }
  }
}
