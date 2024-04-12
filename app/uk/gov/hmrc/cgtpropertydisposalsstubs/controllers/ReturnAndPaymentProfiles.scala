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

import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesListReturnsResponse.{Charge, ReturnSummary}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesAddressDetails, DesFinancialTransactionItem, FinancialTransaction}
import uk.gov.hmrc.time.TaxYear

import java.time.LocalDate

object ReturnAndPaymentProfiles {
  final case class ReturnProfile(returnSummary: ReturnSummary, financialData: List[FinancialTransaction])

  final case class AccountProfile(cgtReferencePredicate: String => Boolean, returns: List[ReturnProfile])

  private final val currentTaxYear = TaxYear.current.startYear

  private val currentTaxYearMinus3 = currentTaxYear - 3
  private val currentTaxYearMinus2 = currentTaxYear - 2
  private val currentTaxYearMinus1 = currentTaxYear - 1

  /* Account 1 for CGT refs ending with 1 */
  private val account1: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111111"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000001",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress =
            DesAddressDetails("1 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus3, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23520"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111112"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000002",
          submissionDate = LocalDate.of(currentTaxYearMinus1, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus1, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 1",
          propertyAddress =
            DesAddressDetails("Acme Ltd", Some("1 Similar Place"), Some("Southampton"), None, Some("S12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23555"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus1, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23555"),
            outstandingAmount = BigDecimal("23555"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23555"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return3 = {
      val originalChargeReference = "XCRG3333333333"
      val penaltyChargeReference  = "XCRG4444444444"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000003",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            addressLine1 = "14 Something Something Something",
            addressLine2 = Some("That Other Place"),
            addressLine3 = None,
            addressLine4 = None,
            postalCode = Some("ZZ0 0ZZ"),
            countryCode = "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus3, 10, 5),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYearMinus1, 1, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("1000"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("680"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 1, 31))
                )
              )
            )
          )
        )
      )
    }

    val return4 = {
      val originalChargeReference = "XCRG3333333334"
      val penaltyChargeReference  = "XCRG4444444445"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000004",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            addressLine1 = "14 Something Something Something",
            addressLine2 = Some("That Other Place"),
            addressLine3 = None,
            addressLine4 = None,
            postalCode = Some("ZZ0 0ZZ"),
            countryCode = "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus3, 10, 5),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYearMinus1, 5, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("350"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("650"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("650"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Outgoing Payment"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("680"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 5, 31))
                )
              )
            )
          )
        )
      )
    }

    val return5 = {
      val originalChargeReference = "XCRG3333333335"
      val penaltyChargeReference  = "XCRG4444444446"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000005",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            addressLine1 = "14 Something Something Something",
            addressLine2 = Some("That Other Place"),
            addressLine3 = None,
            addressLine4 = None,
            postalCode = Some("ZZ0 0ZZ"),
            countryCode = "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus3, 10, 5),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYearMinus1, 1, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Some Unknown Clearing Reason"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("680"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 1, 31))
                )
              )
            )
          )
        )
      )
    }

    val return6 = {
      val originalChargeReference = "XCRG3333333336"
      val penaltyChargeReference  = "XCRG4444444447"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000006",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            addressLine1 = "14 Something Something Something",
            addressLine2 = Some("That Other Place"),
            addressLine3 = None,
            addressLine4 = None,
            postalCode = Some("ZZ0 0ZZ"),
            countryCode = "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus3, 10, 5),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYearMinus1, 1, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Mass Write-Off"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Automatic Clearing"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 1, 31))
                )
              )
            )
          )
        )
      )
    }

    val return7 =
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000007",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            addressLine1 = "14 Something Something Something",
            addressLine2 = Some("That Other Place"),
            addressLine3 = None,
            addressLine4 = None,
            postalCode = Some("ZZ0 0ZZ"),
            countryCode = "GB"
          ),
          totalCGTLiability = BigDecimal("0"),
          charges = None
        ),
        List.empty
      )

    val return8 = {
      val chargeReference = "XCRG9999999999"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000011",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress =
            DesAddressDetails("2 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("43520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus3, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("43520"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("43520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("43520"),
                  paymentMethod = Some("Invalid Payment Method"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Write-Off"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.endsWith("1"), List(return1, return2, return3, return4, return5, return6, return7, return8))
  }

  /* Account 2 for CGT refs starting with XD */
  private val account2: AccountProfile = {
    val chargeReference = "XCRG1111111110"

    val return1 =
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000001",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 25),
          lastUpdatedDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 2)),
          taxYear = "currentTaxYear - 3",
          propertyAddress =
            DesAddressDetails("2 Not sure Where", Some("Don't know what I'm doing"), None, None, Some("ZZ0 0ZZ"), "GB"),
          totalCGTLiability = BigDecimal("1725"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus1, 1, 31),
                chargeReference = chargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal(1000),
            outstandingAmount = BigDecimal(1000),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal(1000),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 7, 30))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal(725),
            outstandingAmount = BigDecimal(725),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal(725),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 1, 31))
                )
              )
            )
          )
        )
      )
    AccountProfile(_.startsWith("XD"), List(return1))

  }

  /* Account 3 for CGT ref XDCGTP123456702 */
  private val account3: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111111"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000001",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress =
            DesAddressDetails("1 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus3, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23520"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111112"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000002",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress =
            DesAddressDetails("Acme Ltd", Some("1 Similar Place"), Some("Southampton"), None, Some("S12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus3, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23520"),
            outstandingAmount = BigDecimal("23520"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return3 = {
      val originalChargeReference = "XCRG3333333333"
      val penaltyChargeReference  = "XCRG4444444444"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000003",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus3, 10, 5),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYearMinus1, 1, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("1000"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("680"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 1, 31))
                )
              )
            )
          )
        )
      )
    }

    val return4 = {
      val originalChargeReference = "XCRG3333333334"
      val penaltyChargeReference  = "XCRG4444444445"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000004",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus3, 10, 19),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYearMinus1, 5, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("350"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("650"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 19))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("650"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Outgoing Payment"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 19))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("680"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 5, 31))
                )
              )
            )
          )
        )
      )
    }

    val return5 = {
      val originalChargeReference = "XCRG3333333335"
      val penaltyChargeReference  = "XCRG4444444446"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000005",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            addressLine1 = "14 Something Something Something",
            addressLine2 = Some("That Other Place"),
            addressLine3 = None,
            addressLine4 = None,
            postalCode = Some("ZZ0 0ZZ"),
            countryCode = "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus3, 10, 5),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYearMinus1, 1, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Some Unknown Clearing Reason"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("680"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 1, 31))
                )
              )
            )
          )
        )
      )
    }

    val return6 = {
      val originalChargeReference = "XCRG3333333336"
      val penaltyChargeReference  = "XCRG4444444447"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000006",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            addressLine1 = "14 Something Something Something",
            addressLine2 = Some("That Other Place"),
            addressLine3 = None,
            addressLine4 = None,
            postalCode = Some("ZZ0 0ZZ"),
            countryCode = "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus3, 10, 5),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYearMinus1, 1, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("1000"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Mass Write-Off"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Automatic Clearing"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 1, 31))
                )
              )
            )
          )
        )
      )
    }

    val return7 =
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000007",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            addressLine1 = "14 Something Something Something",
            addressLine2 = Some("That Other Place"),
            addressLine3 = None,
            addressLine4 = None,
            postalCode = Some("ZZ0 0ZZ"),
            countryCode = "GB"
          ),
          totalCGTLiability = BigDecimal("0"),
          charges = None
        ),
        List.empty
      )

    val return8 = {
      val chargeReference = "XCRG9999999991"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000011",
          submissionDate = LocalDate.of(currentTaxYearMinus2, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus2, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 2",
          propertyAddress =
            DesAddressDetails("2 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("43520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus2, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("43520"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("43520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 6, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("43520"),
                  paymentMethod = Some("Invalid Payment Method"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus2, 5, 25)),
                  clearingReason = Some("Write-Off"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return9 = {
      val chargeReference = "XCRG9999999992"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000012",
          submissionDate = LocalDate.of(currentTaxYearMinus2, 7, 1),
          completionDate = LocalDate.of(currentTaxYearMinus2, 6, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 2",
          propertyAddress = DesAddressDetails(
            "2 Similar Place Second",
            Some("Random Avenue"),
            Some("Ipswich"),
            None,
            Some("IP12 1AX"),
            "GB"
          ),
          totalCGTLiability = BigDecimal("47520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus2, 7, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("47520"),
            outstandingAmount = BigDecimal("47520"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("47520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 7, 24))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(
      _.equals("XDCGTP123456702"),
      List(return1, return2, return3, return4, return5, return6, return7, return8, return9)
    )
  }

  /* Account 4 for CGT Ref XXCGTP999999999 */
  private val account4: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111191"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000091",
          submissionDate = LocalDate.of(currentTaxYearMinus2, 4, 1),
          completionDate = LocalDate.of(currentTaxYearMinus2, 3, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 2",
          propertyAddress =
            DesAddressDetails("1 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("235200"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus2, 3, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("235200"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("235200"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 3, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("235200"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus2, 3, 25)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 3, 24))
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111192"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000092",
          submissionDate = LocalDate.of(currentTaxYearMinus2, 4, 20),
          completionDate = LocalDate.of(currentTaxYearMinus2, 4, 20),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 2",
          propertyAddress =
            DesAddressDetails("11 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP13 1AX"), "GB"),
          totalCGTLiability = BigDecimal("250000"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus2, 4, 20), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("250000"),
            outstandingAmount = BigDecimal("250000"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("250000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 4, 20))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("250000"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus2, 4, 20)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 4, 20))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.equals("XXCGTP999999999"), List(return1, return2))
  }

  /* Account 5 for CGT Ref XXCGTP999999998 */
  private val account5: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111291"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000091",
          submissionDate = LocalDate.of(currentTaxYear, 4, 9),
          completionDate = LocalDate.of(currentTaxYear, 4, 8),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear",
          propertyAddress =
            DesAddressDetails("99 Some Place", Some("Random Lane"), Some("Blackpool"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("235200"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYear, 3, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("235200"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("235200"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYear, 3, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("235200"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYear, 3, 25)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYear, 3, 24))
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111292"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000092",
          submissionDate = LocalDate.of(currentTaxYear, 4, 7),
          completionDate = LocalDate.of(currentTaxYear, 4, 6),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear",
          propertyAddress = DesAddressDetails(
            "98 Another Place",
            Some("Random Drive"),
            Some("Blackburn"),
            None,
            Some("IP13 1AX"),
            "GB"
          ),
          totalCGTLiability = BigDecimal("250000"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYear, 4, 10), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("250000"),
            outstandingAmount = BigDecimal("250000"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("250000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYear, 4, 10))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("250000"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYear, 4, 10)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYear, 4, 10))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.equals("XXCGTP999999998"), List(return1, return2))
  }

  /* Account 6 for CGT Ref XXCGTP999999988 */
  private val account6: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111391"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000091",
          submissionDate = LocalDate.of(currentTaxYearMinus1, 4, 15),
          completionDate = LocalDate.of(currentTaxYearMinus1, 4, 10),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 1",
          propertyAddress =
            DesAddressDetails("99 Some Place", Some("Random Lane"), Some("Blackpool"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("235200"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus1, 3, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("235200"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("235200"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 3, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("235200"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus1, 3, 25)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 3, 24))
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111492"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000092",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 4, 16),
          completionDate = LocalDate.of(currentTaxYearMinus3, 4, 11),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress = DesAddressDetails(
            "98 Another Place Place",
            Some("Random Driveway"),
            Some("Buckingham"),
            None,
            Some("IP13 1AX"),
            "GB"
          ),
          totalCGTLiability = BigDecimal("250000"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus3, 4, 20), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("250000"),
            outstandingAmount = BigDecimal("250000"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("250000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 4, 20))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("250000"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 4, 20)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 4, 20))
                )
              )
            )
          )
        )
      )
    }

    val return3 = {
      val chargeReference = "XCRG1111111292"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000093",
          submissionDate = LocalDate.of(currentTaxYearMinus2, 4, 16),
          completionDate = LocalDate.of(currentTaxYearMinus2, 4, 11),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 2",
          propertyAddress = DesAddressDetails(
            "98 Another Place",
            Some("Random Drive"),
            Some("Blackburn"),
            None,
            Some("IP13 1AX"),
            "GB"
          ),
          totalCGTLiability = BigDecimal("250000"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus2, 4, 20), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("250000"),
            outstandingAmount = BigDecimal("250000"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("250000"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 4, 20))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("250000"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus2, 4, 20)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 4, 20))
                )
              )
            )
          )
        )
      )
    }

    val return4 = {
      val chargeReference = "XCRG1111111112"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000002",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress =
            DesAddressDetails("Acme Ltd", Some("1 Similar Place"), Some("Southampton"), None, Some("S12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus3, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23520"),
            outstandingAmount = BigDecimal("23520"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return5 = {
      val originalChargeReference = "XCRG3333333334"
      val penaltyChargeReference  = "XCRG4444444445"

      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000004",
          submissionDate = LocalDate.of(currentTaxYearMinus1, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus1, 5, 24),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 1",
          propertyAddress = DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          totalCGTLiability = BigDecimal("1680"),
          charges = Some(
            List(
              Charge(
                chargeDescription = "CGT PPD Return UK Resident",
                dueDate = LocalDate.of(currentTaxYearMinus1, 10, 5),
                chargeReference = originalChargeReference
              ),
              Charge(
                chargeDescription = "CGT PPD Late Filing Penalty",
                dueDate = LocalDate.of(currentTaxYear + 1, 5, 31),
                chargeReference = penaltyChargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = originalChargeReference,
            originalAmount = BigDecimal("1000"),
            outstandingAmount = BigDecimal("350"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("650"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 10, 5))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("650"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus1, 5, 25)),
                  clearingReason = Some("Outgoing Payment"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference = penaltyChargeReference,
            originalAmount = BigDecimal("680"),
            outstandingAmount = BigDecimal("680"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("680"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYear + 1, 5, 31))
                )
              )
            )
          )
        )
      )
    }

    val return6 = {
      val chargeReference = "XCRG1212121212"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000001212",
          submissionDate = LocalDate.of(currentTaxYearMinus2, 4, 10),
          completionDate = LocalDate.of(currentTaxYearMinus2, 4, 5),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 2",
          propertyAddress =
            DesAddressDetails("Big Ltd", Some("1 Dissimilar Place"), Some("Norfolk"), None, Some("S12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("55555"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus2, 4, 12), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("55555"),
            outstandingAmount = BigDecimal("55555"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("55555"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus2, 4, 12))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.equals("XXCGTP999999988"), List(return1, return2, return3, return4, return5, return6))
  }

  /* Account 7 for CGT ref XXCGTP19999988 */
  private val account7: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111111"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000001",
          submissionDate = LocalDate.of(currentTaxYear, 4, 8),
          completionDate = LocalDate.of(currentTaxYear, 4, 7),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear",
          propertyAddress =
            DesAddressDetails("1 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYear, 4, 8), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23520"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYear, 4, 8))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYear, 4, 8)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYear, 4, 8))
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111112"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000002",
          submissionDate = LocalDate.of(currentTaxYear, 4, 8),
          completionDate = LocalDate.of(currentTaxYear, 4, 7),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear",
          propertyAddress =
            DesAddressDetails("Acme Ltd", Some("1 Similar Place"), Some("Southampton"), None, Some("S12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23555"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYear, 4, 11), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23555"),
            outstandingAmount = BigDecimal("23555"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23555"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYear, 4, 11))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.equals("XXCGTP19999988"), List(return1, return2))
  }

  /* Account 8 for CGT Ref XXCGTP19999978 */
  private val account8: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111111"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000001",
          submissionDate = LocalDate.of(currentTaxYearMinus1, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus1, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 1",
          propertyAddress =
            DesAddressDetails("1 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus1, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23520"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 6, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus1, 5, 25)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111112"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000002",
          submissionDate = LocalDate.of(currentTaxYearMinus1, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus1, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 1",
          propertyAddress =
            DesAddressDetails("Acme Ltd", Some("1 Similar Place"), Some("Southampton"), None, Some("S12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23555"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus1, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23555"),
            outstandingAmount = BigDecimal("23555"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23555"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus1, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.equals("XXCGTP19999978"), List(return1, return2))
  }

  /* Account 9 for CGT ref XXCGTP19999928 - no account in ATs uses this ref */
  private val account9: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111111"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000001",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress =
            DesAddressDetails("1 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23520"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus3, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23520"),
            outstandingAmount = BigDecimal("0"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                ),
                DesFinancialTransactionItem(
                  amount = BigDecimal("23520"),
                  paymentMethod = Some("TPS RECEIPTS BY DEBIT CARD"),
                  clearingDate = Some(LocalDate.of(currentTaxYearMinus3, 5, 25)),
                  clearingReason = Some("Reversal"),
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111112"
      ReturnProfile(
        ReturnSummary(
          submissionId = "000000000002",
          submissionDate = LocalDate.of(currentTaxYearMinus3, 6, 1),
          completionDate = LocalDate.of(currentTaxYearMinus3, 5, 25),
          lastUpdatedDate = None,
          taxYear = "currentTaxYear - 3",
          propertyAddress =
            DesAddressDetails("Acme Ltd", Some("1 Similar Place"), Some("Southampton"), None, Some("S12 1AX"), "GB"),
          totalCGTLiability = BigDecimal("23555"),
          charges = Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(currentTaxYearMinus3, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference = chargeReference,
            originalAmount = BigDecimal("23555"),
            outstandingAmount = BigDecimal("23555"),
            items = Some(
              List(
                DesFinancialTransactionItem(
                  amount = BigDecimal("23555"),
                  paymentMethod = None,
                  clearingDate = None,
                  clearingReason = None,
                  dueDate = Some(LocalDate.of(currentTaxYearMinus3, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.equals("XXCGTP19999928"), List(return1, return2))
  }

  private val profiles: List[AccountProfile] =
    List(account9, account8, account7, account6, account5, account4, account3, account2, account1)

  def getProfile(cgtReference: String): Option[AccountProfile] =
    profiles.find(_.cgtReferencePredicate(cgtReference))
}
