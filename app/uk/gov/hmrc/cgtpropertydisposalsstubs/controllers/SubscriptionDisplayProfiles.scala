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
import cats.implicits._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesErrorResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.{DesSubscriptionDisplayDetails, SubscriptionDetails}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesAddressDetails, DesContactDetails, DesIndividual, DesTrustee}

case class SubscriptionDisplay(
  predicate: String => Boolean,
  subscriptionDisplayResponse: Either[Result, DesSubscriptionDisplayDetails]
)

object SubscriptionDisplayProfiles {

  val individualSubscriptionDisplayDetails = DesSubscriptionDisplayDetails(
    regime = "CGT",
    subscriptionDetails = SubscriptionDetails(
      individual = Some(
        DesIndividual(
          "Individual",
          "Luke",
          "Bishop"
        )
      ),
      None,
      true,
      DesAddressDetails(
        "100 Sutton Street",
        Some("Wokingham"),
        Some("Surrey"),
        Some("London"),
        "DH14EJ",
        "GB"
      ),
      DesContactDetails(
        "Stephen Wood",
        Some("(+013)32752856"),
        Some("(+44)7782565326"),
        Some("01332754256"),
        Some("stephen@abc.co.uk")
      )
    )
  )

  val trusteeSubscriptionDisplayDetails = DesSubscriptionDisplayDetails(
    regime = "CGT",
    subscriptionDetails = SubscriptionDetails(
      None,
      trustee = Some(
        DesTrustee(
          "Trustee",
          "ABC Trust"
        )
      ),
      true,
      DesAddressDetails(
        "101 Kiwi Street",
        None,
        None,
        Some("Christchurch"),
        "",
        "NZ"
      ),
      DesContactDetails(
        "Stephen Wood",
        Some("(+013)32752856"),
        Some("(+44)7782565326"),
        Some("01332754256"),
        Some("stephen@abc.co.uk")
      )
    )
  )

  def getDisplayDetails(id: String): Option[SubscriptionDisplay] =
    subscriptionDisplayProfiles.find(_.predicate(id))

  private val subscriptionDisplayProfiles = List(
    SubscriptionDisplay(
      _ === "XACGTP123456789",
      Right(individualSubscriptionDisplayDetails)
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456790",
      Right(trusteeSubscriptionDisplayDetails)
    ),
    SubscriptionDisplay(
      id => id.startsWith("ERG400"),
      Left(
        BadRequest(
          desErrorResponse("INVALID_REGIME", "Submission has not passed validation. Invalid parameter regimeValue")
        )
      )
    ),
    SubscriptionDisplay(
      id => id.startsWith("EID400"),
      Left(
        BadRequest(
          desErrorResponse("INVALID_IDTYPE", "Submission has not passed validation. Invalid parameter idType.")
        )
      )
    ),
    SubscriptionDisplay(
      id => id.startsWith("ERQ400"),
      Left(
        BadRequest(
          desErrorResponse(
            "INVALID_IDREQUEST",
            "Submission has not passed validation. Request not implemented by the backend."
          )
        )
      )
    ),
    SubscriptionDisplay(
      id => id.startsWith("ECID400"),
      Left(
        BadRequest(
          desErrorResponse("INVALID_CORRELATION", "Submission has not passed validation. Invalid CorrelationId.")
        )
      )
    ),
    SubscriptionDisplay(
      id => id.startsWith("ER404"),
      Left(
        BadRequest(desErrorResponse("NOT_FOUND", "Data not found for the provided Registration Number"))
      )
    ),
    SubscriptionDisplay(
      id => id.startsWith("ER500"),
      Left(
        BadRequest(
          desErrorResponse(
            "SERVER_ERROR",
            "DES is currently experiencing problems that require live service intervention"
          )
        )
      )
    ),
    SubscriptionDisplay(
      id => id.startsWith("ER503"),
      Left(
        BadRequest(
          desErrorResponse("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding")
        )
      )
    )
  )

  private def desErrorResponse(code: String, reason: String): JsValue = Json.toJson(DesErrorResponse(code, reason))

}
