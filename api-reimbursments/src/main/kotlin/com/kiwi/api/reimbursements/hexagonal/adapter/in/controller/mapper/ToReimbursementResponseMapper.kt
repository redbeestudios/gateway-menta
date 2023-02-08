package com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.model.ReimbursementResponse
import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.model.provider.MaskedPanProvider
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToReimbursementResponseMapper(
    private val maskedPanProvider: MaskedPanProvider
) {

    fun map(createdRefund: CreatedRefund) =
        with(createdRefund) {
            ReimbursementResponse(
                id = id.toString(),
                acquirerId = data.acquirerId,
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
                    displayMessage = authorization.displayMessage?.message,
                    retrievalReferenceNumber = authorization.retrievalReferenceNumber
                ),
                merchant = ReimbursementResponse.Merchant(
                    id = data.merchant.id
                ),
                terminal = ReimbursementResponse.Terminal(
                    serialCode = data.terminal.serialCode,
                    id = data.terminal.id,
                    softwareVersion = data.terminal.softwareVersion
                ),
                capture = ReimbursementResponse.Capture(
                    card = ReimbursementResponse.Capture.Card(
                        maskedPan = maskPan(data.capture.card.pan),
                        holder = ReimbursementResponse.Capture.Card.Holder(
                            name = data.capture.card.holder.name,
                            identification = data.capture.card.holder.identification?.let {
                                ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        bank = data.capture.card.bank,
                        type = data.capture.card.type,
                        brand = data.capture.card.brand,
                        iccData = data.capture.card.iccData
                    ),
                    inputMode = data.capture.inputMode,
                    previousTransactionInputMode = data.capture.previousTransactionInputMode
                ),
                amount = ReimbursementResponse.Amount(
                    total = data.amount.total,
                    currency = data.amount.currency,
                    breakdown = data.amount.breakdown.map {
                        ReimbursementResponse.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                installments = data.installments,
                trace = data.trace,
                ticket = data.ticket,
                batch = data.batch,
                hostMessage = "hostMessage",
                datetime = data.datetime
            )
        }.log { info("refund response mapped: {}", it) }

    fun map(createdAnnulment: CreatedAnnulment) =
        with(createdAnnulment) {
            ReimbursementResponse(
                id = id.toString(),
                acquirerId = data.acquirerId,
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
                    displayMessage = authorization.displayMessage?.message,
                    retrievalReferenceNumber = authorization.retrievalReferenceNumber
                ),
                merchant = ReimbursementResponse.Merchant(
                    id = data.merchant.id
                ),
                terminal = ReimbursementResponse.Terminal(
                    serialCode = data.terminal.serialCode,
                    id = data.terminal.id,
                    softwareVersion = data.terminal.softwareVersion
                ),
                capture = ReimbursementResponse.Capture(
                    card = ReimbursementResponse.Capture.Card(
                        maskedPan = maskPan(data.capture.card.pan),
                        holder = ReimbursementResponse.Capture.Card.Holder(
                            name = data.capture.card.holder.name,
                            identification = data.capture.card.holder.identification?.let {
                                ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        bank = data.capture.card.bank,
                        type = data.capture.card.type,
                        brand = data.capture.card.brand,
                        iccData = data.capture.card.iccData
                    ),
                    inputMode = data.capture.inputMode,
                    previousTransactionInputMode = data.capture.previousTransactionInputMode
                ),
                amount = ReimbursementResponse.Amount(
                    total = data.amount.total,
                    currency = data.amount.currency,
                    breakdown = data.amount.breakdown.map {
                        ReimbursementResponse.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                installments = data.installments,
                trace = data.trace,
                ticket = data.ticket,
                batch = data.batch,
                hostMessage = "hostMessage",
                datetime = data.datetime
            )
        }.log { info("annulment response mapped: {}", it) }

    private fun maskPan(pan: String?) =
        pan?.let { maskedPanProvider.provide(it) }

    companion object : CompanionLogger()
}
