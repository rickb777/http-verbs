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

package uk.gov.hmrc.play.http.ws

import play.api.libs.json.{Json, Writes}
import play.api.mvc.Results
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.play.http.{Precondition, HeaderCarrier, HttpPost, HttpResponse}

import scala.concurrent.Future


trait WSPost extends HttpPost with WSRequest {

  override def doPost[A](url: String, body: A, precondition: Precondition, headers: Seq[(String, String)])
                        (implicit rds: Writes[A], hc: HeaderCarrier): Future[HttpResponse] = {
    buildRequest(url, precondition).withHeaders(headers: _*).post(Json.toJson(body)).map(new WSHttpResponse(_))
  }

  override def doFormPost(url: String, body: Map[String, Seq[String]], precondition: Precondition)
                         (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    buildRequest(url, precondition).post(body).map(new WSHttpResponse(_))
  }

  override def doPostString(url: String, body: String, precondition: Precondition, headers: Seq[(String, String)])
                           (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    buildRequest(url, precondition).withHeaders(headers: _*).post(body).map(new WSHttpResponse(_))
  }

  override def doEmptyPost[A](url: String, precondition: Precondition)
                             (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    import play.api.http.Writeable._
    buildRequest(url, precondition).post(Results.EmptyContent()).map(new WSHttpResponse(_))
  }

}
