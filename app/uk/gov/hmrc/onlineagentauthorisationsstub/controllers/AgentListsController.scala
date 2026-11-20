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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.onlineagentauthorisationsstub.models.requests.CountAgentListsRequest
import uk.gov.hmrc.onlineagentauthorisationsstub.util.StubResource
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton()
class AgentListsController @Inject()(cc: ControllerComponents)
  extends BackendController(cc) with StubResource {

  import AgentListsController.*

  def countAgentLists: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CountAgentListsRequest].fold(
      invalid => {
        logger.error(s"[AgentListsController][countAgentLists]: Failed to validate payload, errors: $invalid")
        Future.successful(BadRequest(Json.obj("message" -> s"Invalid payload: $invalid")))
      },
      payload =>
        payload.agentCode match {
          case ErrorAgentCode =>
            logger.info("[AgentListsController][countAgentLists]: Returning a stubbed 500")
            Future.successful(InternalServerError(Json.obj("message" -> "Stubbed internal server error")))
          case agentCode =>
            val agentPath = s"$basePath/$agentCode/$fileName"
            val path      = if (agentCode.matches(SafeAgentCode) && resourceExists(agentPath)) agentPath else defaultPath
            logger.info(s"[AgentListsController][countAgentLists]: Returning $path")
            Future.successful(jsonResourceAsResponse(path))
        }
    )
  }
}

object AgentListsController {
  val ErrorAgentCode: String        = "INTERNALSERVERERROR"
  private val SafeAgentCode: String = "[A-Za-z0-9]+"
  private val basePath: String      = "/resources.agentLists"
  private val fileName: String      = "countAgentLists.json"
  val defaultPath: String           = s"$basePath/default/$fileName"
}
