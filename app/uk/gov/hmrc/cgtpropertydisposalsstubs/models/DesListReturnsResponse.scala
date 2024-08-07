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

package uk.gov.hmrc.cgtpropertydisposalsstubs.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesListReturnsResponse.ReturnSummary

import java.time.{LocalDate, LocalDateTime}

final case class DesListReturnsResponse(
  processingDate: LocalDateTime,
  returnList: List[ReturnSummary]
)

object DesListReturnsResponse {

  final case class Charge(
    chargeDescription: String,
    dueDate: LocalDate,
    chargeReference: String
  )

  final case class ReturnSummary(
    submissionId: String,
    submissionDate: LocalDate,
    completionDate: LocalDate,
    lastUpdatedDate: Option[LocalDate],
    taxYear: String,
    propertyAddress: DesAddressDetails,
    totalCGTLiability: BigDecimal,
    charges: Option[List[Charge]]
  )

  implicit val chargeFormat: OFormat[Charge]                                = Json.format
  implicit val returnFormat: OFormat[ReturnSummary]                         = Json.format
  implicit val desListReturnResponseFormat: OFormat[DesListReturnsResponse] = Json.format

}
