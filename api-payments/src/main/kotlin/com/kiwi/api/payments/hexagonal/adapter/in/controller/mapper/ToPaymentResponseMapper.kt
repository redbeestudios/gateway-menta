package com.kiwi.api.payments.hexagonal.adapter.`in`.controller.mapper

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.model.PaymentResponse
import com.kiwi.api.payments.hexagonal.domain.CreatedPayment
import com.kiwi.api.payments.hexagonal.domain.provider.MaskPanProvider
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToPaymentResponseMapper(
    private val maskPanProvider: MaskPanProvider
) {
    fun map(createdPayment: CreatedPayment) =
        with(createdPayment) {
            PaymentResponse(
                id = id,
                ticketId = ticketId,
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
                        maskedPan = data.capture.card.pan?.mask(),
                        holder = PaymentResponse.Capture.Card.Holder(
                            name = data.capture.card.holder.name,
                            identification = data.capture.card.holder.identification?.let {
                                PaymentResponse.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        iccData = data.capture.card.iccData,
                        bank = data.capture.card.bank,
                        type = data.capture.card.type,
                        brand = data.capture.card.brand,
                    ),
                    inputMode = data.capture.inputMode,
                    previousTransactionInputMode = data.capture.previousTransactionInputMode
                ),
                amount = PaymentResponse.Amount(
                    total = data.amount.total,
                    currency = data.amount.currency,
                    breakdown = data.amount.breakdown.map {
                        PaymentResponse.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                installments = data.installments,
                trace = data.trace,
                batch = data.batch,
                hostMessage = "host message", // TODO: Ver que hacer con este campo
                ticket = data.ticket,
                terminal = PaymentResponse.Terminal(
                    id = data.terminal.id.toString(),
                    serialCode = data.terminal.serialCode,
                    softwareVersion = data.terminal.softwareVersion
                ),
                merchant = PaymentResponse.Merchant(
                    id = data.merchant.id.toString()
                ),
                datetime = data.datetime
            )
        }.log { info("payment response mapped: {}", it) }

    private fun String.mask() =
        maskPanProvider.provide(this)
            .log { info("pan masked") }

    companion object : CompanionLogger()
}
