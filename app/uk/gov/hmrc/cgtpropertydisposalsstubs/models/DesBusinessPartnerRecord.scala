/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.libs.json.{Format, Json, Reads, Writes}

case class DesBusinessPartnerRecord(
  address: DesAddressDetails,
  contactDetails: DesBusinessPartnerRecord.DesContactDetails,
  sapNumber: SapNumber,
  organisation: Option[DesOrganisation],
  individual: Option[DesIndividual]
)

object DesBusinessPartnerRecord {
  final case class DesContactDetails(emailAddress: Option[String])

  implicit val contactDetailsWrites: Writes[DesContactDetails] = Json.writes[DesContactDetails]
  implicit val bprWrites: Writes[DesBusinessPartnerRecord]     = Json.writes[DesBusinessPartnerRecord]
}

case class DesOrganisation(
  organisationName: String
)

object DesOrganisation {
  implicit val organisationWrites: Format[DesOrganisation] = Json.format[DesOrganisation]
}

case class DesIndividual(
  firstName: String,
  lastName: String
)

object DesIndividual {
  implicit val individualWrites: Format[DesIndividual] = Json.format[DesIndividual]
}
