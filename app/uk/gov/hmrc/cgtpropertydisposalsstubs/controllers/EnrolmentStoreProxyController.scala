/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.cgtpropertydisposalsstubs.controllers

import akka.actor.ActorSystem
import com.google.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.EnrolmentRequest
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

class EnrolmentStoreProxyController @Inject()(
  cc: ControllerComponents,
  system: ActorSystem
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def allocateEnrolmentToGroup(groupId: String, enrolmentKey: String): Action[AnyContent] = Action { implicit request =>
    request.body.asJson.fold[Result] {
      logger.warn("Could not find enrolment action payload")
      BadRequest
    } { enrolmentAction =>
      enrolmentAction
        .validate[EnrolmentRequest]
        .fold(
          { error =>
            logger.warn(s"Invalid JSON payload: [payload : $error]")
            BadRequest
          }, { enrolmentAction =>
            (groupId, enrolmentKey) match {
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567800") => Created
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567801") => BadRequest("SERVICE_INACTIVE")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567802") => BadRequest("AUTO_ACTIVATION_NOT_POSSIBLE")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567803") => Forbidden("INVALID_CREDENTIAL_TYPE")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567804") => BadRequest("INVALID_JSON_BODY")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567805") => BadRequest("INVALID_QUERY_PARAMETERS")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567806") => BadRequest("INVALID_GROUP_ID")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567807") => BadRequest("INVALID_AGENT_FORMAT")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567808") => Forbidden("AGENT_AUTHORISATION")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567809") => Conflict("ALLOCATION_EXISTS")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567810") => Conflict("MULTIPLE_ENROLMENTS_INVALID")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567811") => BadRequest("INACTIVE_AGENT")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567812") => BadRequest("TOO_MANY_AGENTS")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567813") => NotFound("GROUP_DOES_NOT_EXIST")
              case ("ABCEDEFGI123400", "HMRC-CGT-PD~1234567814") => BadRequest("INVALID_KNOWN_FACTS_SUPPLIED")
            }
          }
        )
    }
  }

}
