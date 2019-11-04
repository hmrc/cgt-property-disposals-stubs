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

import cats.instances.either._
import cats.syntax.either._
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesBusinessPartnerRecord
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesBusinessPartnerRecord.{DesContactDetails, DesIndividual, DesOrganisation}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.SubscriptionResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesErrorResponse.desErrorResponseJson
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.SubscriptionStatusResponse.SubscriptionStatus
import uk.gov.hmrc.cgtpropertydisposalsstubs.models._

case class Profile(
  predicate: Either[Either[TRN, SAUTR], NINO] => Boolean,
  bprResponse: Either[Result, DesBusinessPartnerRecord],
  subscriptionStatusResponse: Option[Either[Result, SubscriptionStatusResponse]],
  subscriptionResponse: Option[Either[Result, SubscriptionResponse]]
)

object SubscriptionProfiles {

  implicit class IdOps(id: Either[Either[TRN, SAUTR], NINO]) {

    def isANinoAnd(p: NINO => Boolean): Boolean = id.exists(p)

    def isAnSautrAnd(p: SAUTR => Boolean): Boolean = id.swap.exists(_.exists(p))

    def isATrnAnd(p: TRN => Boolean): Boolean = id.swap.exists(_.swap.exists(p))
  }

  def getProfile(id: Either[Either[TRN, SAUTR], NINO]): Option[Profile] =
    profiles.find(_.predicate(id))

  def getProfile(sapNumber: SapNumber): Option[Profile] =
    profiles.find(_.bprResponse.exists(_.sapNumber == sapNumber))

  private val profiles: List[Profile] = {
    def bpr(sapNumber: SapNumber) = DesBusinessPartnerRecord(
      DesAddressDetails("3rd Wick Street", None, None, None, "JW123ST", "GB"),
      DesContactDetails(Some("testCGT@email.com")),
      sapNumber,
      None,
      Some(DesIndividual("John", "Wick"))
    )

    val subscriptionResponse = SubscriptionResponse("XACGTP123456789")

    val (lukeBishopContactDetails, lukeBishopBpr) = {
      val contactDetails = DesContactDetails(Some("luke.bishop@email.com"))

      contactDetails -> DesBusinessPartnerRecord(
        DesAddressDetails("65 Tuckers Road", Some("North London"), None, None, "NR38 3EX", "GB"),
        contactDetails,
        SapNumber("0100042628"),
        None,
        Some(DesIndividual("Luke", "Bishop"))
      )
    }

    val notSubscribedStatusResponse = SubscriptionStatusResponse(SubscriptionStatus.NotSubscribed)

    List(
      Profile(
        _ === Right(NINO("CG123456D")),
        Right(bpr(SapNumber("1234567890"))),
        Some(Right(notSubscribedStatusResponse)),
        Some(Right(subscriptionResponse))
      ),
      Profile(
        _ === Right(NINO("AB123456C")),
        Right(lukeBishopBpr),
        None,
        Some(Right(SubscriptionResponse("XYCGTP001000170")))
      ),
      Profile(
        _.isANinoAnd(_.value.startsWith("EM000")),
        Right(lukeBishopBpr.copy(contactDetails = lukeBishopContactDetails.copy(emailAddress = None))),
        Some(Right(notSubscribedStatusResponse)),
        None
      ),
      Profile(
        _.isAnSautrAnd(_.value.endsWith("89")),
        Right(
          lukeBishopBpr.copy(
            contactDetails = lukeBishopContactDetails.copy(emailAddress = None),
            organisation   = Some(DesOrganisation("Plip Plop Trusts")),
            individual     = None
          )
        ),
        Some(Right(notSubscribedStatusResponse)),
        None
      ),
      Profile(
        _.isAnSautrAnd(_.value.endsWith("99")),
        Right(
          lukeBishopBpr.copy(
            contactDetails = lukeBishopContactDetails.copy(emailAddress = None)
          )
        ),
        Some(Right(notSubscribedStatusResponse)),
        None
      ),
      Profile(
        id => id.isANinoAnd(_.value.startsWith("ER400")) || id.isAnSautrAnd(_.value.endsWith("5400")),
        Left(
          BadRequest(
            desErrorResponseJson("INVALID_NINO", "Submission has not passed validation. Invalid parameter NINO")
          )
        ),
        None,
        None
      ),
      Profile(
        id => id.isANinoAnd(_.value.startsWith("ER404")) || id.isAnSautrAnd(_.value.endsWith("5404")),
        Left(
          NotFound(desErrorResponseJson("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
        ),
        None,
        None
      ),
      Profile(
        id => id.isANinoAnd(_.value.startsWith("ER409")) || id.isAnSautrAnd(_.value.endsWith("5409")),
        Left(Conflict(desErrorResponseJson("CONFLICT", "The remote endpoint has indicated Duplicate Submission"))),
        None,
        None
      ),
      Profile(
        id => id.isANinoAnd(_.value.startsWith("ER500")) || id.isAnSautrAnd(_.value.endsWith("5500")),
        Left(
          InternalServerError(
            desErrorResponseJson(
              "SERVER_ERROR",
              "DES is currently experiencing problems that require live service intervention"
            )
          )
        ),
        None,
        None
      ),
      Profile(
        id => id.isANinoAnd(_.value.startsWith("ER503")) || id.isAnSautrAnd(_.value.endsWith("5503")),
        Left(
          ServiceUnavailable(
            desErrorResponseJson("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding")
          )
        ),
        None,
        None
      ),
      Profile(
        _.isANinoAnd(_.value.startsWith("ES400")),
        Right(bpr(sapNumberForSubscriptionStatus(400))),
        Some(Right(notSubscribedStatusResponse)),
        Some(Left(BadRequest))
      ),
      Profile(
        _.isANinoAnd(_.value.startsWith("ES404")),
        Right(bpr(sapNumberForSubscriptionStatus(404))),
        Some(Right(notSubscribedStatusResponse)),
        Some(Left(NotFound))
      ),
      Profile(
        _.isANinoAnd(_.value.startsWith("ES409")),
        Right(bpr(sapNumberForSubscriptionStatus(409))),
        Some(Right(notSubscribedStatusResponse)),
        Some(Left(Conflict))
      ),
      Profile(
        _.isANinoAnd(_.value.startsWith("ES500")),
        Right(bpr(sapNumberForSubscriptionStatus(500))),
        Some(Right(notSubscribedStatusResponse)),
        Some(Left(InternalServerError))
      ),
      Profile(
        _.isANinoAnd(_.value.startsWith("ES503")),
        Right(bpr(sapNumberForSubscriptionStatus(503))),
        Some(Right(notSubscribedStatusResponse)),
        Some(Left(ServiceUnavailable))
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB01")) ||
            id.isAnSautrAnd(_.value.endsWith("5801")) ||
            id.isATrnAnd(_.value.startsWith("5801")),
        Right(bpr(SapNumber("5801000000"))),
        Some(Right(SubscriptionStatusResponse(
          SubscriptionStatus.Subscribed,
          Some("ZCGT"),
          Some("XACGTP000000000")
        ))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB02")) ||
            id.isAnSautrAnd(_.value.endsWith("5802")) ||
            id.isATrnAnd(_.value.startsWith("5802")),
        Right(bpr(SapNumber("5802000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.RegistrationFormReceived))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB03")) ||
            id.isAnSautrAnd(_.value.endsWith("5803")) ||
            id.isATrnAnd(_.value.startsWith("5803")),
        Right(bpr(SapNumber("5803000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.SentToDs))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB04")) ||
            id.isAnSautrAnd(_.value.endsWith("5804")) ||
            id.isATrnAnd(_.value.startsWith("5804")),
        Right(bpr(SapNumber("5804000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.DsOutcomeInProgress))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB05")) ||
            id.isAnSautrAnd(_.value.endsWith("5805")) ||
            id.isATrnAnd(_.value.startsWith("5805")),
        Right(bpr(SapNumber("5805000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.Rejected))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB06")) ||
            id.isAnSautrAnd(_.value.endsWith("5806")) ||
            id.isATrnAnd(_.value.startsWith("5806")),
        Right(bpr(SapNumber("5806000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.InProcessing))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB07")) ||
            id.isAnSautrAnd(_.value.endsWith("5807")) ||
            id.isATrnAnd(_.value.startsWith("5807")),
        Right(bpr(SapNumber("5807000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.CreateFailed))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB08")) ||
            id.isAnSautrAnd(_.value.endsWith("5808")) ||
            id.isATrnAnd(_.value.startsWith("5808")),
        Right(bpr(SapNumber("5808000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.Withdrawal))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB09")) ||
            id.isAnSautrAnd(_.value.endsWith("5809")) ||
            id.isATrnAnd(_.value.startsWith("5809")),
        Right(bpr(SapNumber("5809000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.SentToRcm))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB10")) ||
            id.isAnSautrAnd(_.value.endsWith("5810")) ||
            id.isATrnAnd(_.value.startsWith("5810")),
        Right(bpr(SapNumber("5810000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.ApprovedWithConditions))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB11")) ||
            id.isAnSautrAnd(_.value.endsWith("5811")) ||
            id.isATrnAnd(_.value.startsWith("5811")),
        Right(bpr(SapNumber("5811000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.Revoked))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB12")) ||
            id.isAnSautrAnd(_.value.endsWith("5812")) ||
            id.isATrnAnd(_.value.startsWith("5812")),
        Right(bpr(SapNumber("5812000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.Deregistered))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB13")) ||
            id.isAnSautrAnd(_.value.endsWith("5813")) ||
            id.isATrnAnd(_.value.startsWith("5813")),
        Right(bpr(SapNumber("5813000000"))),
        Some(Right(SubscriptionStatusResponse(SubscriptionStatus.ContractObjectInactive))),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB14")) ||
            id.isAnSautrAnd(_.value.endsWith("5814")) ||
            id.isATrnAnd(_.value.startsWith("5814")),
        Right(bpr(SapNumber("5814000000"))),
        Some(
          Left(
            BadRequest(
              desErrorResponseJson(
                "INVALID_REGIME",
                "Submission has not passed validation. Invalid parameter regime."
              )
            )
          )
        ),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB15")) ||
            id.isAnSautrAnd(_.value.endsWith("5815")) ||
            id.isATrnAnd(_.value.startsWith("5815")),
        Right(bpr(SapNumber("5815000000"))),
        Some(
          Left(
            BadRequest(
              desErrorResponseJson(
                "INVALID_BPNUMBER",
                "Submission has not passed validation. Invalid parameter bpNumber."
              )
            )
          )
        ),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB16")) ||
            id.isAnSautrAnd(_.value.endsWith("5816")) ||
            id.isATrnAnd(_.value.startsWith("5816")),
        Right(bpr(SapNumber("5816000000"))),
        Some(
          Left(
            BadRequest(
              desErrorResponseJson(
                "INVALID_CORRELATIONID",
                "Submission has not passed validation. Invalid header CorrelationId."
              )
            )
          )
        ),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB17")) ||
            id.isAnSautrAnd(_.value.endsWith("5817")) ||
            id.isATrnAnd(_.value.startsWith("5817")),
        Right(bpr(SapNumber("5817000000"))),
        Some(
          Left(
            NotFound(
              desErrorResponseJson(
                "NOT_FOUND",
                "No Record found for the provided BP Number."
              )
            )
          )
        ),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB18")) ||
            id.isAnSautrAnd(_.value.endsWith("5818")) ||
            id.isATrnAnd(_.value.startsWith("5818")),
        Right(bpr(SapNumber("5818000000"))),
        Some(
          Left(
            InternalServerError(
              desErrorResponseJson(
                "SERVER_ERROR",
                "DES is currently experiencing problems that require live service intervention."
              )
            )
          )
        ),
        None
      ),
      Profile(
        id =>
          id.isANinoAnd(_.value.startsWith("SB19")) ||
            id.isAnSautrAnd(_.value.endsWith("5819")) ||
            id.isATrnAnd(_.value.startsWith("5819")),
        Right(bpr(SapNumber("5819000000"))),
        Some(
          Left(
            ServiceUnavailable(
              desErrorResponseJson(
                "SERVICE_UNAVAILABLE",
                "Dependent systems are currently not responding."
              )
            )
          )
        ),
        None
      )
    )
  }

  def sapNumberForSubscriptionStatus(status: Int): SapNumber =
    SapNumber(("0" * 7) + status)

}
