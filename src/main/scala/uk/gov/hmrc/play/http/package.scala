/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play

import uk.gov.hmrc.play.http.logging.ConnectionTracing

package object http {
  @deprecated("Re-named to ConnectionTracing", "23/04/2014")
  type ConnectionLogging = ConnectionTracing

  // TODO do we really need to keep this?
  @deprecated("moved to uk.gov.hmrc.play.http.reads", "5/4/15")
  type HttpReads[O] = reads.HttpReads[O]
  @deprecated("moved to uk.gov.hmrc.play.http.reads", "5/4/15")
  val HttpReads = reads.HttpReads
}
