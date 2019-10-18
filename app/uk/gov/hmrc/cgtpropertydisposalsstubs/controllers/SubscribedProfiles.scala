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
import play.api.mvc.Result
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.Address.UkAddress
import uk.gov.hmrc.cgtpropertydisposalsstubs.models._

case class SubscribedData(
  cgtReference: String,
  displayResponse: Either[Result, SubscribedDetails]
)

object SubscribedProfiles {

  private val subscribedData = List(
    SubscribedData(
      "XACGTP123456789",
      Right(
        SubscribedDetails(
          Right(IndividualName("Luke", "Bishop")),
          Email("luke.bishop@gmail.com"),
          UkAddress(
            "65 Tuckers Road",
            None,
            Some("North London"),
            Some("GB"),
            "NR38 3EX"
          ),
          ContactName("Luke Bishop"),
          CgtReference("XACGTP123456789"),
          Some(TelephoneNumber("09090909090909")),
          true
        )
      )
    )
  )


//  if (id === "XFCGT123456789") {
//    Ok("")
//  } else if (regime === "NOT-CGT") {
//    BadRequest(
//      Json.toJson(
//        DesErrorResponse2("INVALID_REGIME", "Submission has not passed validation. Invalid parameter regimeValue.")
//      )
//    )
//  } else if (id === "1") {
//    BadRequest(
//      Json.toJson(
//        DesErrorResponse2("INVALID_IDVALUE", "Submission has not passed validation. Invalid parameter idValue.")
//      )
//    )
//  } else if (id === "XFCGT123456789") {
//    BadRequest(
//      Json.toJson(
//        DesErrorResponse2(
//          "INVALID_REQUEST",
//          "Submission has not passed validation. Request not implemented by the backend."
//        )
//      )
//    )
//  } else if (id === "COrrelatio") {
//    BadRequest(
//      Json.toJson(
//        DesErrorResponse2(
//          "INVALID_CORRELATIONID",
//          "Submission has not passed validation. Invalid header CorrelationId."
//        )
//      )
//    )
//  } else if (id === "Not found") {
//    NotFound(Json.toJson(DesErrorResponse2("NOT_FOUND", "Data not found for the provided Registration Number.")))
//  } else if (id === "X1") {
//    InternalServerError(
//      Json.toJson(
//        DesErrorResponse2(
//          "SERVER_ERROR",
//          "DES is currently experiencing problems that require live service intervention."
//        )
//      )
//    )
//  } else if (id === "c1") {
//    ServiceUnavailable(
//      Json.toJson(DesErrorResponse2("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding."))
//    )
//  } else {
//    InternalServerError(Json.toJson(DesErrorResponse2("UNKNOWN_DES_RESPONSE", "Unknown response received from DES")))
//  }
}
