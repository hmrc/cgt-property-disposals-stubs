/*
 * Copyright 2024 HM Revenue & Customs
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

import cats.data.EitherT
import cats.instances.option._
import com.google.inject.{Inject, Singleton}
import org.scalacheck.Gen
import play.api.libs.json.{Json, OFormat, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.{SubscriptionResponse, SubscriptionUpdateResponse}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.SubscriptionStatusResponse.SubscriptionStatus
import uk.gov.hmrc.cgtpropertydisposalsstubs.models._
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.smartstub._

import java.time.LocalDateTime

@Singleton
class SubscriptionController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  def getSubscriptionStatus(sapNumber: String): Action[AnyContent] =
    Action { _ =>
      SubscriptionProfiles
        .getProfile(SapNumber(sapNumber))
        .flatMap(_.subscriptionStatusResponse)
        .getOrElse(Right(SubscriptionStatusResponse(SubscriptionStatus.NotSubscribed)))
        .map(status => Ok(Json.toJson(status)))
        .merge
    }

  def getSubscriptionDetails(id: String): Action[AnyContent] =
    Action { _ =>
      SubscriptionDisplayProfiles
        .getDisplayDetails(id)
        .map(_.subscriptionDisplayResponse.map(displayDetails => Ok(Json.toJson(displayDetails))).merge)
        .getOrElse {
          Ok(
            Json.toJson(
              SubscriptionDisplayProfiles.individualSubscriptionDisplayDetails(registeredWithId = true)
            )
          )
        }
    }

  def updateSubscriptionDetails(id: String): Action[AnyContent] =
    Action { implicit request =>
      request.body.asJson.fold[Result] {
        logger.warn("Could not find JSON in body for subscribe update request")
        BadRequest
      } { json =>
        json
          .validate[SubscriptionUpdateRequest]
          .fold[Result](
            { e =>
              logger.warn(s"Could not validate subscription update body: $e")
              BadRequest
            },
            { subscriptionUpdateRequest =>
              val result: Result =
                EitherT(
                  SubscriptionUpdateProfiles
                    .updateSubscriptionDetails(id)
                    .map(_.subscriptionUpdateResponse)
                ).map(subscriptionResponse => Ok(Json.toJson(subscriptionResponse)))
                  .merge
                  .getOrElse(
                    Ok(
                      Json.toJson(
                        SubscriptionUpdateResponse(
                          "CGT",
                          LocalDateTime.now().toString,
                          "0134567910",
                          id,
                          subscriptionUpdateRequest.subscriptionDetails.addressDetails.countryCode,
                          subscriptionUpdateRequest.subscriptionDetails.addressDetails.postalCode
                        )
                      )
                    )
                  )

              logger.info(s"Returning result $result to subscribe request ${json.toString()}")
              result
            }
          )
      }
    }

  def subscribe(): Action[AnyContent] =
    Action { implicit request =>
      request.body.asJson.fold[Result] {
        logger.warn("Could not find JSON in body for subscribe request")
        BadRequest
      } { json =>
        (json \ "identity" \ "idValue")
          .validate[SapNumber]
          .fold[Result](
            { e =>
              logger.warn(s"Could not validate or find sap number in json for subscribe request: $e")
              BadRequest
            },
            { sapNumber =>
              val result =
                EitherT(SubscriptionProfiles.getProfile(sapNumber).flatMap(_.subscriptionResponse))
                  .map(subscriptionResponse => Ok(Json.toJson(subscriptionResponse)))
                  .merge
                  .getOrElse(Ok(Json.toJson(SubscriptionResponse(randomCgtReferenceId()))))

              logger.info(s"Returning result $result to subscribe request ${json.toString()}")
              result
            }
          )
      }
    }

  private val cgtReferenceIdGen: Gen[String] = for {
    letter <- Gen.alphaUpperChar
    digits <- Gen.listOfN(9, Gen.numChar)
  } yield s"X${letter}CGTP${digits.mkString("")}"

  // sap numbers should be a series of digits so toLong should be ok
  implicit val sapNumberToLong: ToLong[SapNumber] = (i: SapNumber) => i.value.toLong

  private def randomCgtReferenceId(): String =
    cgtReferenceIdGen.sample.get

}

object SubscriptionController {

  case class SubscriptionResponse(cgtReferenceNumber: String)

  object SubscriptionResponse {

    implicit val write: Writes[SubscriptionResponse] = Json.writes[SubscriptionResponse]

  }

  final case class DesSubscriptionDisplayDetails(
    regime: String,
    subscriptionDetails: SubscriptionDetails
  )

  case class SubscriptionDetails(
    typeOfPersonDetails: DesTypeOfPersonDetails,
    isRegisteredWithId: Boolean,
    addressDetails: DesAddressDetails,
    contactDetails: DesContactDetails
  )

  object SubscriptionDetails {
    implicit val dd: OFormat[SubscriptionDetails] = Json.format[SubscriptionDetails]
  }

  object DesSubscriptionDisplayDetails {
    implicit val format: OFormat[DesSubscriptionDisplayDetails] = Json.format[DesSubscriptionDisplayDetails]
  }

  final case class SubscriptionUpdateResponse(
    regime: String,
    processingDate: String,
    formBundleNumber: String,
    cgtReferenceNumber: String,
    countryCode: String,
    postalCode: Option[String]
  )

  object SubscriptionUpdateResponse {
    implicit val format: OFormat[SubscriptionUpdateResponse] = Json.format[SubscriptionUpdateResponse]
  }

}
