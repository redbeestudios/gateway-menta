package com.menta.api.feenicia.adapter.controller.model

import com.menta.api.feenicia.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.feenicia.adapter.controller.models.OperationResponse
import com.menta.api.feenicia.adapter.controller.provider.MaskedPanProvider
import com.menta.api.feenicia.application.aCreatedOperation
import com.menta.api.feenicia.application.anOperationRequest
import com.menta.api.feenicia.domain.ResponseCode.APPROVED
import com.menta.api.feenicia.domain.ResponseCode.FAILED
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToOperationResponseMapperSpec : FeatureSpec({

    feature("map response") {

        val maskedPanProvider = MaskedPanProvider()
        val mapper = ToOperationResponseMapper(maskedPanProvider)

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val createdOperation = aCreatedOperation()
            val operationRequest = anOperationRequest()

            mapper.map(createdOperation, operationRequest) shouldBe with(createdOperation) {
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
                            bank = operationRequest.capture.card.bank,
                            brand = operationRequest.capture.card.brand,
                            type = operationRequest.capture.card.type,
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
                                description = "",
                                amount = operationRequest.amount.total
                            )
                        },
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
                        code = authnum,
                        retrievalReferenceNumber = transactionId ?: "",
                        status = OperationResponse.Status(
                            code = APPROVED.name,
                            situation = OperationResponse.Status.Situation(
                                id = responseCode,
                                description = ""
                            )
                        )
                    ),
                    displayMessage = null,
                    merchant = OperationResponse.Merchant(
                        id = operationRequest.merchant.id
                    )
                )
            }
        }

        scenario("unsuccessful mapping") {
            val createdOperation = aCreatedOperation().copy(responseCode = "A001")
            val operationRequest = anOperationRequest()

            mapper.map(createdOperation, operationRequest) shouldBe with(createdOperation) {
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
                            bank = operationRequest.capture.card.bank,
                            brand = operationRequest.capture.card.brand,
                            type = operationRequest.capture.card.type,
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
                                description = "",
                                amount = operationRequest.amount.total
                            )
                        },
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
                        code = authnum,
                        retrievalReferenceNumber = transactionId ?: "",
                        status = OperationResponse.Status(
                            code = FAILED.name,
                            situation = OperationResponse.Status.Situation(
                                id = responseCode,
                                description = ""
                            )
                        )
                    ),
                    displayMessage = null,
                    merchant = OperationResponse.Merchant(
                        id = operationRequest.merchant.id
                    )
                )
            }
        }
    }
})
