/*
 * Copyright 2015 HM Revenue & Customs
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

package uk.gov.hmrc.play.http

import play.api.http.HttpVerbs.{POST => POST_VERB}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.play.http.Precondition._
import uk.gov.hmrc.play.http.hooks.HttpHooks
import uk.gov.hmrc.play.http.logging.ConnectionTracing
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.Future

trait HttpPost extends HttpVerb with ConnectionTracing with HttpHooks {

  protected def doPost[A](url: String, body: A, precondition: Precondition, headers: Seq[(String, String)])
                         (implicit wts: Writes[A], hc: HeaderCarrier): Future[HttpResponse]

  protected def doPostString(url: String, body: String, precondition: Precondition = NoPrecondition, headers: Seq[(String, String)])
                            (implicit hc: HeaderCarrier): Future[HttpResponse]

  protected def doEmptyPost[A](url: String, precondition: Precondition)
                              (implicit hc: HeaderCarrier): Future[HttpResponse]

  protected def doFormPost(url: String, body: Map[String, Seq[String]], precondition: Precondition)
                          (implicit hc: HeaderCarrier): Future[HttpResponse]

  def POST[I, O](url: String, body: I, precondition: Precondition = NoPrecondition, headers: Seq[(String, String)] = Seq.empty)
                (implicit wts: Writes[I], rds: HttpReads[O], hc: HeaderCarrier): Future[O] = {
    withTracing(POST_VERB, url) {
      val httpResponse = doPost(url, body, precondition, headers)
      executeHooks(url, POST_VERB, Option(Json.stringify(wts.writes(body))), httpResponse)
      mapErrors(POST_VERB, url, httpResponse).map(rds.read(POST_VERB, url, _))
    }
  }

  def POSTString[O](url: String, body: String, precondition: Precondition = NoPrecondition, headers: Seq[(String, String)] = Seq.empty)
                   (implicit rds: HttpReads[O], hc: HeaderCarrier): Future[O] = {
    withTracing(POST_VERB, url) {
      val httpResponse = doPostString(url, body, precondition, headers)
      executeHooks(url, POST_VERB, Option(body), httpResponse)
      mapErrors(POST_VERB, url, httpResponse).map(rds.read(POST_VERB, url, _))
    }
  }

  def POSTForm[O](url: String, body: Map[String, Seq[String]], precondition: Precondition = NoPrecondition)
                 (implicit rds: HttpReads[O], hc: HeaderCarrier): Future[O] = {
    withTracing(POST_VERB, url) {
      val httpResponse = doFormPost(url, body, precondition)
      executeHooks(url, POST_VERB, Option(body), httpResponse)
      mapErrors(POST_VERB, url, httpResponse).map(rds.read(POST_VERB, url, _))
    }
  }

  def POSTEmpty[O](url: String, precondition: Precondition = NoPrecondition)
                  (implicit rds: HttpReads[O], hc: HeaderCarrier): Future[O] = {
    withTracing(POST_VERB, url) {
      val httpResponse = doEmptyPost(url, precondition)
      executeHooks(url, POST_VERB, None, httpResponse)
      mapErrors(POST_VERB, url, httpResponse).map(rds.read(POST_VERB, url, _))
    }
  }
}
