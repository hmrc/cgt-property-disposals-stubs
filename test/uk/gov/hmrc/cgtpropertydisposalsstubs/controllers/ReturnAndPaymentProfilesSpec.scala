/*
 * Copyright 2026 HM Revenue & Customs
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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.OptionValues

class ReturnAndPaymentProfilesSpec extends AnyWordSpecLike with Matchers with OptionValues {

  private val serviceChargeTypesCgtReference = "XPCGTP123456790"

  private val expectedServiceChargeTypes = Set(
    "CGT PPD Interest",
    "CGT PPD Late Filing Penalty",
    "CGT PPD 6 Mth LFP",
    "CGT PPD 12 Mth LFP",
    "CGT PPD Late Payment Penalty",
    "CGT PPD 6 Mth LPP",
    "CGT PPD 12 Mth LPP",
    "CGT PPD Penalty Interest"
  )

  "ReturnAndPaymentProfiles" must {
    "provide a dedicated return for every penalty and service charge type" in {
      val profile = ReturnAndPaymentProfiles.getProfile(serviceChargeTypesCgtReference).value

      val serviceChargeTypes = profile.returns.flatMap(
        _.returnSummary.charges
          .getOrElse(List.empty)
          .map(_.chargeDescription)
          .filterNot(description =>
            description == "CGT PPD Return UK Resident" || description == "CGT PPD Return Non UK Resident"
          )
      )

      profile.returns should have size expectedServiceChargeTypes.size
      serviceChargeTypes.toSet shouldBe expectedServiceChargeTypes
      serviceChargeTypes should have size expectedServiceChargeTypes.size
    }

    "provide matching financial data for every charge" in {
      val profile = ReturnAndPaymentProfiles.getProfile(serviceChargeTypesCgtReference).value

      profile.returns.foreach { returnProfile =>
        returnProfile.returnSummary.charges.value.foreach { charge =>
          val matchingTransactions = returnProfile.financialData.filter(_.chargeReference == charge.chargeReference)

          withClue(s"Charge ${charge.chargeReference}: ") {
            matchingTransactions should have size 1
            matchingTransactions.head.originalAmount should be > BigDecimal(0)
            matchingTransactions.head.items.value.exists(_.dueDate.contains(charge.dueDate)) shouldBe true
          }
        }
      }
    }

    "use unique submission IDs and charge references" in {
      val profile = ReturnAndPaymentProfiles.getProfile(serviceChargeTypesCgtReference).value

      val submissionIds   = profile.returns.map(_.returnSummary.submissionId)
      val chargeReferences = profile.returns.flatMap(_.returnSummary.charges.value.map(_.chargeReference))

      submissionIds.distinct should have size submissionIds.size
      chargeReferences.distinct should have size chargeReferences.size
    }
  }
}
