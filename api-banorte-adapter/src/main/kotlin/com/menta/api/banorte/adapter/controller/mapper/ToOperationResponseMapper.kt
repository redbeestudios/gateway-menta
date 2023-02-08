package com.menta.api.banorte.adapter.controller.mapper

import com.menta.api.banorte.adapter.controller.models.OperationRequest
import com.menta.api.banorte.adapter.controller.models.OperationResponse
import com.menta.api.banorte.adapter.controller.provider.MaskedPanProvider
import com.menta.api.banorte.domain.CreatedOperation
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
                        maskedPan = maskedPanProvider.provide(operationRequest.capture.card.pan),
                        workingKey = null,
                        bank = card.bank,
                        brand = card.brand,
                        type = card.type,
                        nationality = null
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
                    },
                ),
                datetime = custReqDate,
                trace = controlNumber,
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
                    retrievalReferenceNumber = referenceNumber,
                    status = OperationResponse.Status(
                        code = paywResult.description,
                        situation = OperationResponse.Status.Situation(
                            id = authResult.code,
                            description = text
                        )
                    )
                ),
                displayMessage = paywCode?.let {
                    OperationResponse.DisplayMessage(
                        useCode = it,
                        message = ""
                    )
                },
                merchant = OperationResponse.Merchant(
                    id = affiliationId
                )
            )
        }

    private fun maskPan(pan: String?) =
        maskedPanProvider.provide(pan)
}
