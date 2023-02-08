package com.kiwi.api.reverse.hexagonal.adapter.controller.mapper

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.BatchCloseResponse
import com.kiwi.api.reverse.hexagonal.adapter.controller.model.PaymentResponse
import com.kiwi.api.reverse.hexagonal.adapter.controller.model.ReimbursementResponse
import com.kiwi.api.reverse.hexagonal.application.*
import com.kiwi.api.reverse.hexagonal.domain.provider.PanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ResponseMapperSpec : FeatureSpec({

    feature("map a payment response") {

        val panProvider = mockk<PanProvider>()
        val mapper = ResponseMapper(panProvider)

        scenario("successful mapping") {

            val payment = aPayment()
            val maskedPan = "XXXXXXXXXXXX2030"
            every { panProvider.provideMaskedFrom(payment.capture.card) } returns maskedPan


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
                            maskedPan = maskedPan,
                            iccData = capture.card.iccData,
                            cardSequenceNumber = capture.card.cardSequenceNumber,
                            bank = capture.card.bank,
                            type = capture.card.type,
                            brand = capture.card.brand,
                            holder = PaymentResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = PaymentResponse.Capture.Card.Holder.Identification(
                                    number = capture.card.holder.identification.number,
                                    type = capture.card.holder.identification.type
                                )
                            ),

                            ),
                        inputMode = capture.inputMode,
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

        val panProvider = mockk<PanProvider>()
        val mapper = ResponseMapper(panProvider)

        scenario("successful mapping") {

            val annulment = anAnnulment()
            val maskedPan = "XXXXXXXXXXXX2030"

            every { panProvider.provideMaskedFrom(annulment.capture.card) } returns maskedPan

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
                            maskedPan = maskedPan,
                            iccData = capture.card.iccData,
                            cardSequenceNumber = capture.card.cardSequenceNumber,
                            bank = capture.card.bank,
                            type = capture.card.type,
                            brand = capture.card.brand,
                            holder = ReimbursementResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = capture.card.holder.identification.number,
                                    type = capture.card.holder.identification.type
                                )
                            ),

                            ),
                        inputMode = capture.inputMode,
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
                    datetime = datetime,
                    installments = installments
                )
            }
        }
    }

    feature("map a refund response") {

        val panProvider = mockk<PanProvider>()
        val mapper = ResponseMapper(panProvider)

        scenario("successful mapping") {

            val refund = aRefund()
            val maskedPan = "XXXXXXXXXXXX2030"

            every { panProvider.provideMaskedFrom(refund.capture.card) } returns maskedPan

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
                            maskedPan = maskedPan,
                            iccData = capture.card.iccData,
                            cardSequenceNumber = capture.card.cardSequenceNumber,
                            bank = capture.card.bank,
                            type = capture.card.type,
                            brand = capture.card.brand,
                            holder = ReimbursementResponse.Capture.Card.Holder(
                                name = capture.card.holder.name,
                                identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = capture.card.holder.identification.number,
                                    type = capture.card.holder.identification.type
                                )
                            ),

                            ),
                        inputMode = capture.inputMode,
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
                    datetime = datetime,
                    installments = installments,
                )
            }
        }
    }

    feature("map a batch close response") {

        val panProvider = mockk<PanProvider>()
        val mapper = ResponseMapper(panProvider)

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
                    ticket = ticket,
                    total = BatchCloseResponse.Total(
                        amount = totalAmount,
                        currency = currency,
                        operationCode = total.operationCode
                    )
                )
            }
        }
    }
})
