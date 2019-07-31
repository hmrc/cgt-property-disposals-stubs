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

import java.time.LocalDate

import com.google.inject.Inject
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesBusinessPartnerRecord
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesBusinessPartnerRecord.{DesAddress, DesContactDetails, DesIndividual}
import uk.gov.hmrc.play.bootstrap.controller.BackendController

class BusinessPartnerRecordController @Inject()(cc: ControllerComponents) extends BackendController(cc) {

  def getBusinessPartnerRecord(nino: String): Action[AnyContent] = Action { implicit request =>
    val individual = DesIndividual("Paulo", "Miguel", LocalDate.of(1989,1,14))
    val address = DesAddress("First Street", None, None, None, Some("ABC123"), "GB" )
    val contDetails = DesContactDetails(Some("paulomiguel010@gmail.com"))
    val bpr =  DesBusinessPartnerRecord(individual, address, contDetails)
    Ok(Json.toJson(bpr))
  }

}

object BusinessPartnerRecordController {

  import DesBusinessPartnerRecord._

  final case class DesBusinessPartnerRecord(
                                             individual: DesIndividual,
                                             address: DesAddress,
                                             contactDetails: DesContactDetails
                                           )

  object DesBusinessPartnerRecord {
    final case class DesAddress(
                                 addressLine1: String,
                                 addressLine2: Option[String],
                                 addressLine3: Option[String],
                                 addressLine4: Option[String],
                                 postalCode: Option[String],
                                 countryCode: String
                               )

    final case class DesIndividual(
                                    firstName: String,
                                    lastName: String,
                                    dateOfBirth: LocalDate
                                  )

    final case class DesContactDetails(emailAddress: Option[String])

    implicit val addressReads: Writes[DesAddress] = Json.writes[DesAddress]
    implicit val individualReads: Writes[DesIndividual] = Json.writes[DesIndividual]
    implicit val contactDetailsReads: Writes[DesContactDetails] = Json.writes[DesContactDetails]
    implicit val bprReads: Writes[DesBusinessPartnerRecord] = Json.writes[DesBusinessPartnerRecord]
  }


}