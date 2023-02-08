package com.kiwi.api.reverse.hexagonal.adapter.controller.model

import com.kiwi.api.reverse.hexagonal.adapter.controller.mapper.ResponseMapper
import com.kiwi.api.reverse.hexagonal.application.aBatchClose
import com.kiwi.api.reverse.hexagonal.application.aPayment
import com.kiwi.api.reverse.hexagonal.application.aRefund
import com.kiwi.api.reverse.hexagonal.application.anAnnulment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ResponseMapperSpec : FeatureSpec({

    feature("map a payment response") {

        val mapper = ResponseMapper()

        scenario("successful mapping") {

            val payment = aPayment()

            mapper.map(payment) shouldBe with(payment) {
                PaymentResponse(
                    id = id,
                    paymentId = paymentId,
                    status = PaymentResponse.Status(
                        code = authorization.status.code,
                        situation = authorization.status.situation?.let {
                            PaymentResponse.Status.Situation(
                                id = it.id,
                                description = it.description
                            )
                        }
                    ),
                    authorization = PaymentResponse.Authorization(
                        code = authorization.authorizationCode,
                        displayMessage = authorization.displayMessage,
                        retrievalReferenceNumber = authorization.retrievalReferenceNumber
                    ),
                    capture = PaymentResponse.Capture(
                        card = PaymentResponse.Capture.Card(
                            maskedPan = "XXXXXXXXXXXX2030",
                            holder = PaymentResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = PaymentResponse.Capture.Card.Holder.Identification(
                                    number = capture.card.holder.identification.number,
                                    type = capture.card.holder.identification.type
                                )
                            ),

                            ),
                        inputMode = capture.inputMode,
                        iccData = capture.iccData
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
                    hostMessage = hostMessage,
                    ticket = ticket,
                    terminal = PaymentResponse.Terminal(
                        id = terminal.id,
                        serialCode = terminal.serialCode
                    ),
                    merchant = PaymentResponse.Merchant(
                        id = merchant.id
                    ),
                    datetime = dateTime
                )
            }
        }
    }

    feature("map an annulment response") {

        val mapper = ResponseMapper()

        scenario("successful mapping") {

            val annulment = anAnnulment()

            mapper.map(annulment) shouldBe with(annulment) {
                ReimbursementResponse(
                    id = id,
                    paymentId = paymentId,
                    status = ReimbursementResponse.Status(
                        code = authorization.status.code,
                        situation = authorization.status.situation?.let {
                            ReimbursementResponse.Status.Situation(
                                id = it.id,
                                description = it.description
                            )
                        }
                    ),
                    authorization = ReimbursementResponse.Authorization(
                        code = authorization.authorizationCode,
                        displayMessage = authorization.displayMessage,
                        retrievalReferenceNumber = authorization.retrievalReferenceNumber
                    ),
                    capture = ReimbursementResponse.Capture(
                        card = ReimbursementResponse.Capture.Card(
                            maskedPan = capture.card.maskedPan,
                            holder = ReimbursementResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = capture.card.holder.identification.number,
                                    type = capture.card.holder.identification.type
                                )
                            ),

                            ),
                        inputMode = capture.inputMode,
                        iccData = capture.iccData
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
                    hostMessage = hostMessage,
                    ticket = ticket,
                    terminal = ReimbursementResponse.Terminal(
                        id = terminal.id,
                        serialCode = terminal.serialCode
                    ),
                    merchant = ReimbursementResponse.Merchant(
                        id = merchant.id
                    ),
                    datetime = datetime
                )
            }
        }
    }

    feature("map a refund response") {

        val mapper = ResponseMapper()

        scenario("successful mapping") {

            val refund = aRefund()

            mapper.map(refund) shouldBe with(refund) {
                ReimbursementResponse(
                    id = id,
                    paymentId = paymentId,
                    status = ReimbursementResponse.Status(
                        code = authorization.status.code,
                        situation = authorization.status.situation?.let {
                            ReimbursementResponse.Status.Situation(
                                id = it.id,
                                description = it.description
                            )
                        }
                    ),
                    authorization = ReimbursementResponse.Authorization(
                        code = authorization.authorizationCode,
                        displayMessage = authorization.displayMessage,
                        retrievalReferenceNumber = authorization.retrievalReferenceNumber
                    ),
                    capture = ReimbursementResponse.Capture(
                        card = ReimbursementResponse.Capture.Card(
                            maskedPan = capture.card.maskedPan,
                            holder = ReimbursementResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = capture.card.holder.identification.number,
                                    type = capture.card.holder.identification.type
                                )
                            ),

                            ),
                        inputMode = capture.inputMode,
                        iccData = capture.iccData
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
                    hostMessage = hostMessage,
                    ticket = ticket,
                    terminal = ReimbursementResponse.Terminal(
                        id = terminal.id,
                        serialCode = terminal.serialCode
                    ),
                    merchant = ReimbursementResponse.Merchant(
                        id = merchant.id
                    ),
                    datetime = datetime
                )
            }
        }
    }

    feature("map a batch close response") {

        val mapper = ResponseMapper()

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
                        code = authorization.authorizationCode,
                        displayMessage = authorization.displayMessage,
                        retrievalReferenceNumber = authorization.retrievalReferenceNumber
                    ),
                    trace = trace,
                    batch = batch,
                    hostMessage = hostMessage,
                    terminal = BatchCloseResponse.Terminal(
                        id = terminal.id,
                        serialCode = terminal.serialCode
                    ),
                    merchant = BatchCloseResponse.Merchant(
                        id = merchant.id
                    ),
                    datetime = datetime,
                    softwareVersion = softwareVersion,
                    totals = totals
                )
            }
        }
    }
})
