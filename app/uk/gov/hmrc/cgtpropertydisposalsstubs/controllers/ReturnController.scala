/*
 * Copyright 2020 HM Revenue & Customs
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

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesReturnResponse, PPDReturnResponseDetails}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class ReturnController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def createReturn(cgtReferenceNumber: String): Action[JsValue] = Action(parse.json) { request =>
    val submittedReturn: JsResult[(BigDecimal,String)] = for {
      a  <- (request.body \ "ppdReturnDetails" \ "returnDetails" \ "totalLiability" ).validate[BigDecimal]
      d <- (request.body \ "ppdReturnDetails" \ "returnDetails" \ "completionDate").validate[String]
    } yield (a,d)

    val r:(BigDecimal,String) = submittedReturn.asOpt.getOrElse((BigDecimal(0),"2020-02-20"))

    Ok(
      Json.toJson(prepareDesReturnRespose(cgtReferenceNumber, r._1, r._2))
    )
  }

  def prepareDesReturnRespose(cgtReferenceNumber:String, amount:BigDecimal, dueDate:String): DesReturnResponse = {
    val ppdReturnResponseDetails = PPDReturnResponseDetails(
      chargeType = "Late Penalty",
      chargeReference = "XCRG1234567890",
      amount = amount.toDouble,
      dueDate = LocalDate.parse(dueDate).plusDays(30),
      formBundleNumber = "123456789012",
      cgtReferenceNumber = cgtReferenceNumber
    )
    DesReturnResponse(
      processingDate = LocalDate.now(),
      ppdReturnResponseDetails = ppdReturnResponseDetails
    )
  }

}
