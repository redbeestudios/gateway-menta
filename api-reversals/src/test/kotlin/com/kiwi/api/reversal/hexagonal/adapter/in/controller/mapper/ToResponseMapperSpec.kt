package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.mapper

import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.BatchCloseResponse
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.PaymentResponse
import com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model.ReimbursementResponse
import com.kiwi.api.reversal.hexagonal.application.aBatchClose
import com.kiwi.api.reversal.hexagonal.application.aPayment
import com.kiwi.api.reversal.hexagonal.application.aRefund
import com.kiwi.api.reversal.hexagonal.application.anAnnulment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToResponseMapperSpec : FeatureSpec({

    feature("map a payment response") {

        val mapper = ToResponseMapper()

        scenario("successful mapping") {

            val aPayment = aPayment()

            mapper.map(aPayment) shouldBe with(aPayment) {
                PaymentResponse(
                    operationId = operationId?.toString(),
                    capture = PaymentResponse.Capture(
                        card = PaymentResponse.Capture.Card(
                            maskedPan = capture.card.pan,
                            bank = capture.card.bank,
                            type = capture.card.type,
                            brand = capture.card.brand,
                            holder = PaymentResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = capture.card.holder.identification?.let {
                                    PaymentResponse.Capture.Card.Holder.Identification(
                                        number = it.number,
                                        type = it.type
                                    )
                                }
                            ),
                            iccData = capture.card.iccData
                        ),
                        inputMode = capture.inputMode,
                        previousTransactionInputMode = capture.previousTransactionInputMode
                    ),
                    amount = PaymentResponse.Amount(
                        total = amount.total,
                        currency = amount.currency,
                        breakdown = amount.breakdown.map {
                            PaymentResponse.Amount.Breakdown(
                                description = it.description,
                                amount = it.amount
                            )
                        }
                    ),
                    installments = installments,
                    trace = trace,
                    batch = batch,
                    hostMessage = "host message",
                    ticket = ticket,
                    terminal = PaymentResponse.Terminal(
                        id = terminal.id.toString(),
                        serialCode = terminal.serialCode,
                        softwareVersion = terminal.softwareVersion
                    ),
                    merchant = PaymentResponse.Merchant(
                        id = merchant.id.toString()
                    ),
                    datetime = datetime
                )
            }
        }
    }

    feature("map an annulment response") {

        val mapper = ToResponseMapper()

        scenario("successful mapping") {

            val anAnnulment = anAnnulment()

            mapper.map(anAnnulment) shouldBe with(anAnnulment) {
                ReimbursementResponse(
                    operationId = operationId?.toString(),
                    capture = ReimbursementResponse.Capture(
                        card = ReimbursementResponse.Capture.Card(
                            maskedPan = capture.card.pan,
                            holder = ReimbursementResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = capture.card.holder.identification?.let {
                                    ReimbursementResponse.Capture.Card.Holder.Identification(
                                        number = it.number,
                                        type = it.type
                                    )
                                }
                            ),
                            bank = capture.card.bank,
                            type = capture.card.type,
                            brand = capture.card.brand,
                            iccData = capture.card.iccData
                        ),
                        inputMode = capture.inputMode,
                        previousTransactionInputMode = capture.previousTransactionInputMode
                    ),
                    amount = ReimbursementResponse.Amount(
                        total = amount.total,
                        currency = amount.currency,
                        breakdown = amount.breakdown.map {
                            ReimbursementResponse.Amount.Breakdown(
                                description = it.description,
                                amount = it.amount
                            )
                        }
                    ),
                    trace = trace,
                    batch = batch,
                    hostMessage = "host message",
                    ticket = ticket,
                    terminal = ReimbursementResponse.Terminal(
                        id = terminal.id.toString(),
                        serialCode = terminal.serialCode,
                        softwareVersion = terminal.softwareVersion
                    ),
                    merchant = ReimbursementResponse.Merchant(
                        id = merchant.id.toString()
                    ),
                    datetime = datetime,
                    installments = installments,
                )
            }
        }
    }

    feature("map a refund response") {

        val mapper = ToResponseMapper()

        scenario("successful mapping") {

            val maskedPan = "XXXXXXXXXXXX5555"
            val aRefund = aRefund().let {
                it.copy(
                    capture = it.capture.copy(
                        card = it.capture.card.copy(
                            pan = maskedPan
                        )
                    )
                )
            }

            mapper.map(aRefund) shouldBe with(aRefund) {
                ReimbursementResponse(
                    operationId = operationId?.toString(),
                    capture = ReimbursementResponse.Capture(
                        card = ReimbursementResponse.Capture.Card(
                            maskedPan = maskedPan,
                            holder = ReimbursementResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = capture.card.holder.identification?.let {
                                    ReimbursementResponse.Capture.Card.Holder.Identification(
                                        number = it.number,
                                        type = it.type
                                    )
                                }
                            ),

                            bank = capture.card.bank,
                            type = capture.card.type,
                            brand = capture.card.brand,
                            iccData = capture.card.iccData
                        ),
                        inputMode = capture.inputMode,
                        previousTransactionInputMode = capture.previousTransactionInputMode
                    ),
                    amount = ReimbursementResponse.Amount(
                        total = amount.total,
                        currency = amount.currency,
                        breakdown = amount.breakdown.map {
                            ReimbursementResponse.Amount.Breakdown(
                                description = it.description,
                                amount = it.amount
                            )
                        }
                    ),
                    trace = trace,
                    batch = batch,
                    hostMessage = "host message",
                    ticket = ticket,
                    terminal = ReimbursementResponse.Terminal(
                        id = terminal.id.toString(),
                        serialCode = terminal.serialCode,
                        softwareVersion = terminal.softwareVersion
                    ),
                    merchant = ReimbursementResponse.Merchant(
                        id = merchant.id.toString()
                    ),
                    datetime = datetime,
                    installments = installments,
                )
            }
        }
    }

    feature("map a batch close response") {

        val mapper = ToResponseMapper()

        scenario("successful mapping") {

            val refund = aBatchClose()

            mapper.map(refund) shouldBe with(refund) {
                BatchCloseResponse(
                    id = id,
                    status = BatchCloseResponse.Status(
                        code = authorization.status.code,
                        situation = authorization.status.situation?.let {
                            BatchCloseResponse.Status.Situation(
                                id = it.id,
                                description = it.description
                            )
                        }
                    ),
                    authorization = BatchCloseResponse.Authorization(
                        code = authorization.code,
                        displayMessage = authorization.displayMessage,
                        retrievalReferenceNumber = authorization.retrievalReferenceNumber
                    ),
                    trace = trace,
                    batch = batch,
                    hostMessage = hostMessage,
                    terminal = BatchCloseResponse.Terminal(
                        id = terminal.id.toString(),
                        serialCode = terminal.serialCode,
                        softwareVersion = terminal.softwareVersion
                    ),
                    merchant = BatchCloseResponse.Merchant(
                        id = merchant.id.toString()
                    ),
                    ticket = ticket,
                    datetime = datetime,
                    totals = totals.map {
                        BatchCloseResponse.Total(
                            operationCode = it.operationCode,
                            amount = it.amount,
                            currency = it.currency
                        )
                    }
                )
            }
        }
    }
})
