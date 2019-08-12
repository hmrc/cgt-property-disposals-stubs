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

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.SubscriptionResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext
import scala.util.Random

@Singleton
class SubscriptionController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {


  def subscribe(): Action[AnyContent] = Action { implicit request =>
    request.body.asJson.fold[Result]{
      logger.warn("Could not find JSON in body for subscribe request")
      BadRequest
    }{ json =>
      (json \ "sapNumber").validate[String].fold[Result](
        { e =>
          logger.warn(s"Could not find sap number in json for subscribe request: $e")
          BadRequest
        }, {
          case statusRegex(statusString) =>
            logger.info(s"Returning status $statusString to subscribe request")
            Status(statusString.toInt)

          case _ =>
            val cgtRef =  Random.alphanumeric.take(20).toList.mkString("").toUpperCase
            Ok(Json.toJson(SubscriptionResponse(cgtRef)))
        }
      )
    }
  }

  val statusRegex = "555(\\d\\d\\d)\\d{4}".r


}

object  SubscriptionController {

  case class SubscriptionResponse( cgtReferenceNumber : String)

    object SubscriptionResponse {

      implicit val write : Writes[SubscriptionResponse] = Json.writes[SubscriptionResponse]

    }
}
