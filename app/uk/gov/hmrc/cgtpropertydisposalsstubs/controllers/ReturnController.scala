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

import cats.instances.bigDecimal._
import cats.syntax.eq._
import com.eclipsesource.schema.drafts.Version4
import com.eclipsesource.schema.drafts.Version4._
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import com.google.inject.{Inject, Singleton}
import org.scalacheck.Gen
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.config.AppConfig
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesReturn._
import uk.gov.hmrc.cgtpropertydisposalsstubs.models._
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.GenUtils.sample
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import scala.io.Source
import scala.util.Try

@Singleton
class ReturnController @Inject() (cc: ControllerComponents, appConfig: AppConfig)
    extends BackendController(cc)
    with Logging {

  lazy val schemaToBeValidated = Json
    .fromJson[SchemaType](
      Json.parse(
        Source
          .fromInputStream(
            this.getClass.getResourceAsStream("/resources/submit-return-des-schema-v-1-0-0.json")
          )
          .mkString
      )
    )
    .get

  def submitReturn(cgtReferenceNumber: String): Action[JsValue] =
    Action(parse.json) { request =>
      val validator = SchemaValidator(Some(Version4))

      val submittedReturn: JsResult[(BigDecimal, LocalDate, JsValue)] = for {
        a <- (request.body \ "ppdReturnDetails" \ "returnDetails" \ "totalLiability").validate[BigDecimal]
        d <- (request.body \ "ppdReturnDetails" \ "returnDetails" \ "completionDate").validate[LocalDate]
        e <- validator.validate(schemaToBeValidated, request.body)
      } yield (a, d, e)

      submittedReturn.fold(
        { e =>
          logger.warn(s"Could not validate or parse request body: $e")
          BadRequest
        },
        {
          case (taxDue, completionDate, _) =>
            Ok(
              Json.toJson(prepareDesSubmitReturnResponse(cgtReferenceNumber, taxDue, completionDate))
            )
        }
      )
    }

  def listReturns(cgtReference: String, fromDate: String, toDate: String): Action[AnyContent] =
    Action { _ =>
      withFromAndToDate(fromDate, toDate) {
        case (from, to) =>
          Ok(
            Json.toJson(
              DesListReturnsResponse(
                LocalDateTime.now(),
                ReturnAndPaymentProfiles
                  .getProfile(cgtReference)
                  .map(
                    _.returns
                      .map(_.returnSummary)
                      .filter(p => !p.submissionDate.isBefore(from) && !p.submissionDate.isAfter(to))
                  )
                  .getOrElse(List.empty)
              )
            )
          )
      }
    }

  def displayReturn(cgtReference: String, submissionId: String): Action[AnyContent] =
    Action { _ =>
      val cgtRefInit = cgtReference.init

      val desReturn =
        if (cgtRefInit.endsWith("2") && submissionId.nonEmpty)
          dummyMultipleDisposalsReturn
        else if (cgtRefInit.endsWith("3") && submissionId.nonEmpty)
          dummySingleIndirectDisposalReturn
        else if (cgtRefInit.endsWith("4") && submissionId.nonEmpty)
          dummyMultipleIndirectDisposalsReturn
        else if (cgtRefInit.endsWith("5") && submissionId.nonEmpty)
          dummySingleMixedUseDisposalReturn
        else if (cgtRefInit.endsWith("6") && submissionId.nonEmpty)
          dummyMultipleDisposalsResidentialReturn
        else if (cgtRefInit.endsWith("7") && submissionId.nonEmpty)
          dummyMultipleDisposals2021Return
        else if (cgtRefInit.endsWith("8") && submissionId.nonEmpty)
          dummySingleDisposalReturnFor2023SAQuestion
        else if (cgtRefInit.endsWith("9") && submissionId.nonEmpty)
          dummySingleDisposalReturnForSAQuestion
        else dummySingleDisposalReturn
      Ok(Json.toJson(desReturn))
    }

  val dummySingleDisposalReturnFor2023SAQuestion = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2023, 4, 7),
      isUKResident = true,
      numberDisposals = 1,
      totalTaxableGain = BigDecimal(11000),
      totalLiability = BigDecimal(21000),
      totalYTDLiability = BigDecimal(31000),
      estimate = false,
      repayment = false,
      attachmentUpload = false,
      declaration = true,
      countryResidence = Some("GB"),
      attachmentID = None,
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        disposalDate = LocalDate.of(2023, 4, 6),
        addressDetails = DesAddressDetails("You know that place", None, None, None, Some("ZZ0 0ZZ"), "GB"),
        assetType = "res",
        acquisitionType = "bought",
        landRegistry = false,
        acquisitionPrice = BigDecimal(5000),
        rebased = false,
        disposalPrice = BigDecimal(15000),
        improvements = true,
        percentOwned = Some(7300.32),
        acquiredDate = Some(LocalDate.of(2000, 1, 1)),
        rebasedAmount = None,
        disposalType = Some("sold"),
        improvementCosts = Some(1),
        acquisitionFees = Some(3),
        disposalFees = Some(3),
        initialGain = Some(54),
        initialLoss = Some(0)
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(2300)), Some(BigDecimal(600))),
    IncomeAllowanceDetails(BigDecimal(20000.34), Some(BigDecimal(37900)), Some(BigDecimal(500)), None),
    Some(
      ReliefDetails(
        reliefs = true,
        Some(BigDecimal(6000.73)),
        Some(1000.23),
        None,
        Some("none"),
        Some(BigDecimal(0))
      )
    )
  )

  val dummySingleDisposalReturnForSAQuestion = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2021, 3, 20),
      isUKResident = true,
      numberDisposals = 1,
      totalTaxableGain = BigDecimal(11000),
      totalLiability = BigDecimal(21000),
      totalYTDLiability = BigDecimal(31000),
      estimate = false,
      repayment = false,
      attachmentUpload = false,
      declaration = true,
      countryResidence = Some("GB"),
      attachmentID = None,
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        disposalDate = LocalDate.of(2021, 3, 15),
        addressDetails = DesAddressDetails("You know that place", None, None, None, Some("ZZ0 0ZZ"), "GB"),
        assetType = "res",
        acquisitionType = "bought",
        landRegistry = false,
        acquisitionPrice = BigDecimal(5000),
        rebased = false,
        disposalPrice = BigDecimal(15000),
        improvements = true,
        percentOwned = Some(7300.32),
        acquiredDate = Some(LocalDate.of(2000, 1, 1)),
        rebasedAmount = None,
        disposalType = Some("sold"),
        improvementCosts = Some(1),
        acquisitionFees = Some(3),
        disposalFees = Some(3),
        initialGain = Some(54),
        initialLoss = Some(0)
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(2300)), Some(BigDecimal(600))),
    IncomeAllowanceDetails(BigDecimal(20000.34), Some(BigDecimal(37900)), Some(BigDecimal(500)), None),
    Some(
      ReliefDetails(
        reliefs = true,
        privateResRelief = Some(BigDecimal(6000.73)),
        lettingsRelief = Some(1000.23),
        giftHoldOverRelief = None,
        otherRelief = Some("none"),
        otherReliefAmount = Some(BigDecimal(0))
      )
    )
  )

  val dummySingleDisposalReturn = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2020, 4, 20),
      isUKResident = true,
      numberDisposals = 1,
      totalTaxableGain = BigDecimal(100),
      totalLiability = BigDecimal(100),
      totalYTDLiability = BigDecimal(100),
      estimate = false,
      repayment = false,
      attachmentUpload = false,
      declaration = true,
      countryResidence = Some("GB"),
      attachmentID = None,
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        disposalDate = LocalDate.of(2020, 4, 15),
        addressDetails = DesAddressDetails("You know that place", None, None, None, Some("ZZ0 0ZZ"), "GB"),
        assetType = "res",
        acquisitionType = "bought",
        landRegistry = false,
        acquisitionPrice = BigDecimal(50),
        rebased = false,
        disposalPrice = BigDecimal(150),
        improvements = true,
        percentOwned = Some(73.32),
        acquiredDate = Some(LocalDate.of(2000, 1, 1)),
        rebasedAmount = None,
        disposalType = Some("sold"),
        improvementCosts = Some(1),
        acquisitionFees = Some(3),
        disposalFees = Some(3),
        initialGain = Some(54),
        initialLoss = Some(0)
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(23)), Some(BigDecimal(6))),
    IncomeAllowanceDetails(BigDecimal(2.34), Some(BigDecimal(379)), Some(BigDecimal(5)), None),
    Some(
      ReliefDetails(
        reliefs = true,
        privateResRelief = Some(BigDecimal(6.73)),
        lettingsRelief = Some(1.23),
        giftHoldOverRelief = None,
        otherRelief = Some("none"),
        otherReliefAmount = Some(BigDecimal(0))
      )
    )
  )

  val dummyMultipleDisposalsReturn = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2020, 4, 22),
      isUKResident = false,
      numberDisposals = 68,
      totalTaxableGain = BigDecimal(100),
      totalLiability = BigDecimal(100),
      totalYTDLiability = BigDecimal(100),
      estimate = false,
      repayment = false,
      attachmentUpload = true,
      declaration = true,
      countryResidence = Some("NZ"),
      attachmentID = Some("123456789"),
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        disposalDate = LocalDate.of(2020, 4, 10),
        addressDetails = DesAddressDetails("You know that place", None, None, None, Some("ZZ0 0ZZ"), "GB"),
        assetType = "res nonres shares mix",
        acquisitionType = "not captured for multiple disposals",
        landRegistry = false,
        acquisitionPrice = BigDecimal(50),
        rebased = false,
        disposalPrice = BigDecimal(150),
        improvements = false,
        percentOwned = None,
        acquiredDate = Some(LocalDate.of(2000, 1, 1)),
        rebasedAmount = None,
        disposalType = None,
        improvementCosts = None,
        acquisitionFees = None,
        disposalFees = None,
        initialGain = None,
        initialLoss = None
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(23)), Some(BigDecimal(6))),
    IncomeAllowanceDetails(BigDecimal(2.34), None, None, None),
    None
  )

  val dummyMultipleDisposalsResidentialReturn = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2020, 4, 22),
      isUKResident = true,
      numberDisposals = 68,
      totalTaxableGain = BigDecimal(100),
      totalLiability = BigDecimal(100),
      totalYTDLiability = BigDecimal(100),
      estimate = false,
      repayment = false,
      attachmentUpload = true,
      declaration = true,
      countryResidence = Some("GB"),
      attachmentID = Some("123456789"),
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        disposalDate = LocalDate.of(2020, 4, 10),
        addressDetails = DesAddressDetails("You know that place", None, None, None, Some("ZZ0 0ZZ"), "GB"),
        assetType = "res",
        acquisitionType = "not captured for multiple disposals",
        landRegistry = false,
        acquisitionPrice = BigDecimal(50),
        rebased = false,
        disposalPrice = BigDecimal(150),
        improvements = false,
        percentOwned = None,
        acquiredDate = Some(LocalDate.of(2000, 1, 1)),
        rebasedAmount = None,
        disposalType = None,
        improvementCosts = None,
        acquisitionFees = None,
        disposalFees = None,
        initialGain = None,
        initialLoss = None
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(23)), Some(BigDecimal(6))),
    IncomeAllowanceDetails(BigDecimal(2.34), None, None, None),
    None
  )

  val dummyMultipleDisposals2021Return = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2021, 4, 12),
      isUKResident = true,
      numberDisposals = 68,
      totalTaxableGain = BigDecimal(100),
      totalLiability = BigDecimal(100),
      totalYTDLiability = BigDecimal(100),
      estimate = false,
      repayment = false,
      attachmentUpload = true,
      declaration = true,
      countryResidence = Some("GB"),
      attachmentID = Some("123456789"),
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        disposalDate = LocalDate.of(2021, 4, 10),
        addressDetails = DesAddressDetails("You know that place", None, None, None, Some("YY1 1YY"), "GB"),
        assetType = "res",
        acquisitionType = "not captured for multiple disposals",
        landRegistry = false,
        acquisitionPrice = BigDecimal(50),
        rebased = false,
        disposalPrice = BigDecimal(150),
        improvements = false,
        percentOwned = None,
        acquiredDate = Some(LocalDate.of(2000, 1, 1)),
        rebasedAmount = None,
        disposalType = None,
        improvementCosts = None,
        acquisitionFees = None,
        disposalFees = None,
        initialGain = None,
        initialLoss = None
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(23)), Some(BigDecimal(6))),
    IncomeAllowanceDetails(BigDecimal(2.34), None, None, None),
    None
  )

  val dummySingleIndirectDisposalReturn = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2020, 4, 15),
      isUKResident = false,
      numberDisposals = 1,
      totalTaxableGain = BigDecimal(100),
      totalLiability = BigDecimal(100),
      totalYTDLiability = BigDecimal(100),
      estimate = false,
      repayment = false,
      attachmentUpload = false,
      declaration = true,
      countryResidence = Some("HK"),
      attachmentID = None,
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        disposalDate = LocalDate.of(2020, 4, 15),
        addressDetails = DesAddressDetails("Company X", None, None, None, Some("ZZ0 0ZZ"), "IT"),
        assetType = "shares",
        acquisitionType = "bought",
        landRegistry = false,
        acquisitionPrice = BigDecimal(50),
        rebased = false,
        disposalPrice = BigDecimal(150),
        improvements = false,
        percentOwned = Some(100),
        acquiredDate = Some(LocalDate.of(2000, 1, 1)),
        rebasedAmount = None,
        disposalType = Some("sold"),
        improvementCosts = None,
        acquisitionFees = Some(3),
        disposalFees = Some(3),
        initialGain = None,
        initialLoss = None
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(23)), Some(BigDecimal(6))),
    IncomeAllowanceDetails(BigDecimal(2.34), None, None, None),
    None
  )

  val dummyMultipleIndirectDisposalsReturn = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2020, 4, 22),
      isUKResident = false,
      numberDisposals = 68,
      totalTaxableGain = BigDecimal(100),
      totalLiability = BigDecimal(100),
      totalYTDLiability = BigDecimal(100),
      estimate = false,
      repayment = false,
      attachmentUpload = true,
      declaration = true,
      countryResidence = Some("NZ"),
      attachmentID = Some("123456789"),
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        disposalDate = LocalDate.of(2020, 4, 22),
        addressDetails = DesAddressDetails("Some Multiple Company", None, None, None, Some("ZZ0 0ZZ"), "GB"),
        assetType = "shares",
        acquisitionType = "not captured for multiple disposals",
        landRegistry = false,
        acquisitionPrice = BigDecimal(50),
        rebased = false,
        disposalPrice = BigDecimal(150),
        improvements = false,
        percentOwned = Some(BigDecimal(100)),
        acquiredDate = Some(LocalDate.of(2000, 1, 1)),
        rebasedAmount = None,
        disposalType = None,
        improvementCosts = None,
        acquisitionFees = None,
        disposalFees = None,
        initialGain = None,
        initialLoss = None
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(23)), Some(BigDecimal(6))),
    IncomeAllowanceDetails(BigDecimal(2.34), None, None, None),
    None
  )

  val dummySingleMixedUseDisposalReturn = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      customerType = "individual",
      completionDate = LocalDate.of(2020, 4, 22),
      isUKResident = false,
      numberDisposals = 68,
      totalTaxableGain = BigDecimal(100),
      totalLiability = BigDecimal(100),
      totalYTDLiability = BigDecimal(100),
      estimate = false,
      repayment = false,
      attachmentUpload = true,
      declaration = true,
      countryResidence = Some("NZ"),
      attachmentID = Some("123456789"),
      entrepreneursRelief = None,
      valueAtTaxBandDetails = None,
      totalNetLoss = None,
      adjustedAmount = None
    ),
    None,
    List(
      DisposalDetails(
        LocalDate.of(2020, 4, 10),
        DesAddressDetails("You know that place", None, None, None, Some("ZZ0 0ZZ"), "GB"),
        "mix",
        "not captured for single mixed use disposals",
        landRegistry = false,
        BigDecimal(50),
        rebased = false,
        BigDecimal(150),
        improvements = false,
        None,
        Some(LocalDate.of(2000, 1, 1)),
        None,
        Some("sold"),
        None,
        None,
        None,
        None,
        None
      )
    ),
    LossSummaryDetails(inYearLoss = true, preYearLoss = true, Some(BigDecimal(23)), Some(BigDecimal(6))),
    IncomeAllowanceDetails(BigDecimal(2.34), None, None, None),
    None
  )

  private def prepareDesSubmitReturnResponse(
    cgtReferenceNumber: String,
    taxDue: BigDecimal,
    completionDate: LocalDate
  ): DesReturnResponse = {
    val ppdReturnResponseDetails =
      if (cgtReferenceNumber.startsWith("XD"))
        // return with delta charge
        PPDReturnResponseDetails(
          None,
          Some("XCRG1111111110"),
          Some(1725),
          Some(dueDate(completionDate)),
          Some("000000000001"),
          Some(cgtReferenceNumber)
        )
      else if (taxDue =!= BigDecimal(0))
        PPDReturnResponseDetails(
          None,
          Some(randomChargeReference()),
          Some(taxDue.toDouble),
          Some(dueDate(completionDate)),
          Some(randomFormBundleId()),
          Some(cgtReferenceNumber)
        )
      else
        PPDReturnResponseDetails(
          None,
          None,
          Some(BigDecimal(0)),
          None,
          Some(randomFormBundleId()),
          Some(cgtReferenceNumber)
        )
    DesReturnResponse(
      processingDate = LocalDateTime.now(),
      ppdReturnResponseDetails = ppdReturnResponseDetails
    )
  }

  private def dueDate(completionDate: LocalDate): LocalDate = {

    val dueDateChecker = LocalDate.of(
      appConfig.draftReturnNewDueDateStartYear,
      appConfig.draftReturnNewDueDateStartMonth,
      appConfig.draftReturnNewDueDateStartDay - 1
    )

    if (completionDate.isAfter(dueDateChecker))
      completionDate.plusDays(60L)
    else
      completionDate.plusDays(30L)
  }

  private def randomFormBundleId(): String =
    nRandomDigits(12)

  private def randomChargeReference(): String =
    s"XCRG${nRandomDigits(10)}"

  private def nRandomDigits(n: Int): String =
    List.fill(n)(sample(Gen.numChar)).mkString("")

  private def withFromAndToDate(fromDate: String, toDate: String)(f: (LocalDate, LocalDate) => Result): Result = {
    def parseDate(string: String): Option[LocalDate] =
      Try(LocalDate.parse(string, DateTimeFormatter.ISO_DATE)).toOption

    parseDate(fromDate) -> parseDate(toDate) match {
      case (None, None) =>
        logger.warn(s"Could not parse fromDate ('$fromDate') or toDate ('$toDate') ")
        BadRequest

      case (None, Some(_)) =>
        logger.warn(s"Could not parse fromDate ('$fromDate')")
        BadRequest

      case (Some(_), None) =>
        logger.warn(s"Could not parse toDate ('$toDate')")
        BadRequest

      case (Some(from), Some(to)) =>
        f(from, to)
    }
  }

}
