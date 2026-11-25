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

import play.api.Logging
import play.api.http.ContentTypes
import play.api.mvc.{Result, Results}

import java.io.InputStream
import scala.io.Source

/** Reads stub JSON from the classpath (files under conf/). */
trait StubResource extends Results with ContentTypes with Logging {

  def resourceExists(path: String): Boolean =
    Option(getClass.getResource(path)).isDefined

  def jsonResourceAsResponse(path: String): Result =
    findResource(path) match {
      case Some(content) => Ok(content).as(JSON)
      case None          => NotFound
    }

  def findResource(path: String): Option[String] =
    Option(getClass.getResourceAsStream(path)) match {
      case Some(stream) => Some(readStreamToString(stream))
      case None =>
        logger.warn(s"Could not find resource '$path'")
        None
    }

  private def readStreamToString(is: InputStream): String =
    try Source.fromInputStream(is).mkString
    finally is.close()
}
