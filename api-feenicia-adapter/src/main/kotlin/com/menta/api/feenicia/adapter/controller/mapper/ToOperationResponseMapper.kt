package com.menta.api.feenicia.adapter.controller.mapper
import com.menta.api.feenicia.adapter.controller.models.OperationRequest
import com.menta.api.feenicia.adapter.controller.models.OperationResponse
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Amount
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Amount.Breakdown
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Authorization
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Capture
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Capture.Card
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Capture.Card.Holder
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Capture.Card.Holder.Identification
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Merchant
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Status
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Status.Situation
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Terminal
import com.menta.api.feenicia.adapter.controller.provider.MaskedPanProvider
import com.menta.api.feenicia.domain.CreatedOperation
import com.menta.api.feenicia.domain.ResponseCode.APPROVED
import com.menta.api.feenicia.domain.ResponseCode.FAILED
import org.springframework.stereotype.Component

@Component
class ToOperationResponseMapper(
    private val maskedPanProvider: MaskedPanProvider
) {

    fun map(createdOperation: CreatedOperation, operationRequest: OperationRequest) =
        with(createdOperation) {
            OperationResponse(
                capture = Capture(
                    card = Card(
                        holder = Holder(
                            name = operationRequest.capture.card.holder.name,
                            identification = operationRequest.capture.card.holder.identification?.let {
                                Identification(
                                    number = it.number,
                                    type = it.type
                                )
                            }
                        ),
                        maskedPan = maskedPanProvider.provide(operationRequest.capture.card.pan),
                        workingKey = null,
                        bank = operationRequest.capture.card.bank,
                        brand = operationRequest.capture.card.brand,
                        type = operationRequest.capture.card.type,
                        nationality = null
                    ),
                    inputMode = operationRequest.capture.inputMode,
                    previousTransactionInputMode = operationRequest.capture.previousTransactionInputMode
                ),
                amount = Amount(
                    total = operationRequest.amount.total,
                    currency = operationRequest.amount.currency,
                    breakdown = operationRequest.amount.breakdown.map {
                        Breakdown(
                            description = "",
                            amount = operationRequest.amount.total
                        )
                    },
                ),
                datetime = operationRequest.datetime,
                trace = operationRequest.trace,
                ticket = operationRequest.ticket,
                terminal = Terminal(
                    serialCode = operationRequest.terminal.serialCode,
                    id = operationRequest.terminal.id,
                    softwareVersion = operationRequest.terminal.softwareVersion,
                    features = operationRequest.terminal.features
                ),
                batch = operationRequest.batch,
                installments = operationRequest.installments,
                authorization = Authorization(
                    code = authnum,
                    retrievalReferenceNumber = transactionId ?: "",
                    status = Status(
                        code = code(),
                        situation = Situation(
                            id = responseCode,
                            description = ""
                        )
                    )
                ),
                displayMessage = null,
                merchant = Merchant(
                    id = operationRequest.merchant.id
                )
            )
        }

    private fun CreatedOperation.code(): String = if (responseCode == "00") APPROVED.name else FAILED.name
}
