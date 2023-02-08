package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.ReimbursementRequest
import com.kiwi.api.reverse.hexagonal.domain.*
import com.kiwi.api.reverse.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class BuildAnnulmentUseCase {

    fun buildFrom(request: ReimbursementRequest, authorization: Authorization, id: String, merchantId: String) =
        with(request) {
            Annulment(
                id = id,
                paymentId = paymentId,
                authorization = authorization,
                capture = Capture(
                    card =Card(
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
                        holder = Card.Holder(
                            name = capture.card.holder.name,
                            identification = Card.Holder.Identification(
                                number = capture.card.holder.identification.number,
                                type = capture.card.holder.identification.type
                            )
                        ),
                    ),
                    inputMode = capture.inputMode,
                ),
                amount = Amount(
                    total = amount.total,
                    currency = amount.currency,
                    breakdown = amount.breakdown.map {
                        Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                trace = trace,
                batch = batch,
                hostMessage = hostMessage,
                ticket = ticket,
                terminal = Terminal(
                    id = "id terminal",
                    serialCode = terminal.serialCode
                ),
                datetime = datetime,
                merchant = Merchant(
                    id = merchantId
                ),
                installments = installments,
            )
        }
            .log { info("payment built: {}", it) }

    companion object : CompanionLogger()
}
