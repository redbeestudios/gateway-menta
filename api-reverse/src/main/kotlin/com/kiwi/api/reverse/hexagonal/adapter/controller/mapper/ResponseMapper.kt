package com.kiwi.api.reverse.hexagonal.adapter.controller.mapper

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.BatchCloseResponse
import com.kiwi.api.reverse.hexagonal.adapter.controller.model.PaymentResponse
import com.kiwi.api.reverse.hexagonal.adapter.controller.model.ReimbursementResponse
import com.kiwi.api.reverse.hexagonal.domain.*
import com.kiwi.api.reverse.hexagonal.domain.provider.PanProvider
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ResponseMapper(
    private val panProvider: PanProvider
) {


    fun map(reimbursement: Annulment) =
        with(reimbursement) {
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
                        maskedPan = maskPan(capture.card),
                        holder = ReimbursementResponse.Capture.Card.Holder(
                            name = capture.card.holder.name,
                            identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                                number = capture.card.holder.identification.number,
                                type = capture.card.holder.identification.type
                            )
                        ),
                        iccData = capture.card.iccData,
                        cardSequenceNumber = capture.card.cardSequenceNumber,
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand
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
        }.log { info("annulment response mapped: {}", it) }

    fun map(reimbursement: Refund) =
        with(reimbursement) {
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
                        maskedPan =  maskPan(capture.card),
                        holder = ReimbursementResponse.Capture.Card.Holder(
                            name = capture.card.holder.name,
                            identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                                number = capture.card.holder.identification.number,
                                type = capture.card.holder.identification.type
                            )
                        ),
                        iccData = capture.card.iccData,
                        cardSequenceNumber = capture.card.cardSequenceNumber,
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand
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
        }.log { info("refund response mapped: {}", it) }

    fun map(payment: Payment) =
        with(payment) {
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
                        holder = PaymentResponse.Capture.Card.Holder(
                            name = capture.card.holder.name,
                            identification = PaymentResponse.Capture.Card.Holder.Identification(
                                number = capture.card.holder.identification.number,
                                type = capture.card.holder.identification.type
                            )
                        ),
                        maskedPan = maskPan(capture.card),
                        iccData = capture.card.iccData,
                        cardSequenceNumber = capture.card.cardSequenceNumber,
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand
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
        }.log { info("payment response mapped: {}", it) }

    fun map(batchClose: BatchClose) =
        with(batchClose) {
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
                ticket = ticket,
                datetime = datetime,
                softwareVersion = softwareVersion,
                total = BatchCloseResponse.Total(
                    operationCode = total.operationCode,
                    amount = total.amount,
                    currency = total.currency
                )
            )
        }.log { info("batch close response mapped: {}", it) }

    companion object : CompanionLogger()

    private fun maskPan(card: Card) =
        panProvider.provideMaskedFrom(card)
}
