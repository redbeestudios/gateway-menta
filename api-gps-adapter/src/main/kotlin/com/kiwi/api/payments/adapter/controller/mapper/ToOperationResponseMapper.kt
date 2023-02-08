package com.kiwi.api.payments.adapter.controller.mapper

import com.kiwi.api.payments.adapter.controller.models.OperationRequest
import com.kiwi.api.payments.adapter.controller.models.OperationResponse
import com.kiwi.api.payments.adapter.controller.provider.MaskedPanProvider
import com.kiwi.api.payments.domain.CreatedOperation
import org.springframework.stereotype.Component

@Component
class ToOperationResponseMapper(
    private val maskedPanProvider: MaskedPanProvider
) {

    fun map(createdOperation: CreatedOperation, operationRequest: OperationRequest) =
        with(createdOperation) {
            OperationResponse(
                capture = OperationResponse.Capture(
                    card = OperationResponse.Capture.Card(
                        holder = OperationResponse.Capture.Card.Holder(
                            name = operationRequest.capture.card.holder.name,
                            identification = operationRequest.capture.card.holder.identification?.let {
                                OperationResponse.Capture.Card.Holder.Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        iccData = iccData,
                        maskedPan = maskPan(operationRequest.capture.card.pan),
                        workingKey = workingKey,
                        bank = operationRequest.capture.card.bank,
                        brand = operationRequest.capture.card.brand,
                        type = operationRequest.capture.card.type,
                        nationality = cardNationality?.name
                    ),
                    inputMode = operationRequest.capture.inputMode,
                    previousTransactionInputMode = operationRequest.capture.previousTransactionInputMode
                ),
                amount = OperationResponse.Amount(
                    total = operationRequest.amount.total,
                    currency = operationRequest.amount.currency,
                    breakdown = operationRequest.amount.breakdown.map {
                        OperationResponse.Amount.Breakdown(
                            description = it.description,
                            amount = it.amount
                        )
                    }
                ),
                datetime = operationRequest.datetime,
                trace = operationRequest.trace,
                ticket = operationRequest.ticket,
                terminal = OperationResponse.Terminal(
                    serialCode = operationRequest.terminal.serialCode,
                    id = operationRequest.terminal.id,
                    softwareVersion = operationRequest.terminal.softwareVersion,
                    features = operationRequest.terminal.features
                ),
                batch = operationRequest.batch,
                installments = operationRequest.installments,
                authorization = OperationResponse.Authorization(
                    code = authorizationCode,
                    retrievalReferenceNumber = retrievalReferenceNumber,
                    status = OperationResponse.Status(
                        code = responseCode.statusCode.name,
                        situation = OperationResponse.Status.Situation(
                            id = responseCode.code,
                            description = responseCode.name
                        )
                    )
                ),
                displayMessage = displayMessage?.let {
                    OperationResponse.DisplayMessage(
                        useCode = it.useCode.name,
                        message = it.message
                    )
                },
                merchant = OperationResponse.Merchant(
                    id = operationRequest.merchant.id
                )
            )
        }

    private fun maskPan(pan: String) =
        maskedPanProvider.provide(pan)
}
