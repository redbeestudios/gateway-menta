package com.kiwi.api.payments.adapter.controller.mapper

import com.kiwi.api.payments.adapter.controller.models.ReimbursementRequest
import com.kiwi.api.payments.adapter.controller.models.ReimbursementResponse
import com.kiwi.api.payments.adapter.controller.provider.MaskedPanProvider
import com.kiwi.api.payments.domain.CreatedOperation
import org.springframework.stereotype.Component

@Component
class ToReimbursementResponseMapper(
    private val maskedPanProvider: MaskedPanProvider
) {

    fun map(createdOnlineOperation: CreatedOperation, reimbursementRequest: ReimbursementRequest) =
        with(createdOnlineOperation) {
            ReimbursementResponse(
                capture = ReimbursementResponse.Capture(
                    card = ReimbursementResponse.Capture.Card(
                        holder = ReimbursementResponse.Capture.Card.Holder(
                            name = reimbursementRequest.capture.card.holder.name,
                            identification = reimbursementRequest.capture.card.holder.identification?.let {
                                ReimbursementResponse.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        iccData = createdOnlineOperation.iccData,
                        maskedPan = reimbursementRequest.capture.card.pan?.let { maskPan(it) },
                        workingKey = workingKey,
                        bank = reimbursementRequest.capture.card.bank,
                        brand = reimbursementRequest.capture.card.brand,
                        type = reimbursementRequest.capture.card.type,
                        nationality = cardNationality?.name
                    ),
                    inputMode = reimbursementRequest.capture.inputMode,
                    previousTransactionInputMode = reimbursementRequest.capture.previousTransactionInputMode
                ),
                amount = ReimbursementResponse.Amount(
                    total = reimbursementRequest.amount.total,
                    currency = reimbursementRequest.amount.currency,
                    breakdown = reimbursementRequest.amount.breakdown.map {
                        ReimbursementResponse.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                datetime = reimbursementRequest.datetime,
                trace = reimbursementRequest.trace,
                ticket = reimbursementRequest.ticket,
                terminal = ReimbursementResponse.Terminal(
                    serialCode = reimbursementRequest.terminal.serialCode,
                    id = reimbursementRequest.terminal.id,
                    softwareVersion = reimbursementRequest.terminal.softwareVersion,
                    features = reimbursementRequest.terminal.features
                ),
                batch = reimbursementRequest.batch,
                installments = reimbursementRequest.installments,
                authorization = ReimbursementResponse.Authorization(
                    code = authorizationCode,
                    retrievalReferenceNumber = retrievalReferenceNumber,
                    status = ReimbursementResponse.Status(
                        code = responseCode.statusCode.name,
                        situation = ReimbursementResponse.Status.Situation(
                            id = responseCode.code,
                            description = responseCode.name
                        )
                    )
                ),
                displayMessage = displayMessage?.let {
                    ReimbursementResponse.DisplayMessage(
                        useCode = it.useCode.name,
                        message = it.message
                    )
                },
                merchant = ReimbursementResponse.Merchant(
                    id = reimbursementRequest.merchant.id
                )
            )
        }

    private fun maskPan(pan: String) =
        maskedPanProvider.provide(pan)
}
