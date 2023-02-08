package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.PaymentRequest
import com.kiwi.api.reverse.hexagonal.domain.Authorization
import com.kiwi.api.reverse.hexagonal.domain.Card
import com.kiwi.api.reverse.hexagonal.domain.Payment
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class BuildPaymentUseCase {

    fun buildFrom(paymentRequest: PaymentRequest, authorization: Authorization, id: String) =
        with(paymentRequest) {
            Payment(
                id = id,
                paymentId = paymentId,
                authorization = authorization,
                capture = Payment.Capture(
                    card = Card(
                        iccData = capture.card.iccData,
                        cardSequenceNumber = capture.card.cardSequenceNumber,
                        bank = capture.card.bank,
                        type = capture.card.type,
                        brand = capture.card.brand,
                        track1 = capture.card.track1,
                        track2 = capture.card.track2,
                        cvv = capture.card.cvv,
                        expirationDate = capture.card.expirationDate,
                        pan = capture.card.pan,
                        holder =Card.Holder(
                            name = capture.card.holder.name,
                            identification = Card.Holder.Identification(
                                number = capture.card.holder.identification.number,
                                type = capture.card.holder.identification.type
                            )
                        ),
                    ),
                    inputMode = capture.inputMode,
                ),
                amount = Payment.Amount(
                    total = amount.total,
                    currency = amount.currency,
                    breakdown = amount.breakdown.map {
                        Payment.Amount.Breakdown(
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
                terminal = Payment.Terminal(
                    id = "123",
                    serialCode = terminal.serialCode
                ),
                dateTime = datetime,
                merchant = Payment.Merchant(
                    id = "merchantId"
                )
            )
        }
            .log { info("payment built: {}", it) }


    companion object : CompanionLogger()
}
