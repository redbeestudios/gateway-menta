package com.kiwi.api.payments.adapter.controller.mapper

import com.kiwi.api.payments.adapter.controller.models.OperationResponse
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Amount
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Amount.Breakdown
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Authorization
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Capture
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Capture.Card
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Capture.Card.Holder
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Capture.Card.Holder.Identification
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.DisplayMessage
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Status
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Status.Situation
import com.kiwi.api.payments.adapter.controller.models.OperationResponse.Terminal
import com.kiwi.api.payments.adapter.controller.provider.MaskedPanProvider
import com.kiwi.api.payments.application.TestConstants.Companion.AUTHORIZATION_CODE
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_BANK
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_BRAND
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_ICC_DATA
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_MASKED_PAN
import com.kiwi.api.payments.application.TestConstants.Companion.CARD_TYPE
import com.kiwi.api.payments.application.TestConstants.Companion.CURRENCY_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.DATETIME
import com.kiwi.api.payments.application.TestConstants.Companion.IDENTIFICACION_NAME
import com.kiwi.api.payments.application.TestConstants.Companion.IDENTIFICATION_NUMBER
import com.kiwi.api.payments.application.TestConstants.Companion.IDENTIFICATION_TYPE
import com.kiwi.api.payments.application.TestConstants.Companion.INPUT_MODE
import com.kiwi.api.payments.application.TestConstants.Companion.MERCHANT_ID
import com.kiwi.api.payments.application.TestConstants.Companion.OPERATION_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.PREVIOUS_TRANSACTION_INPUT_MODE
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_ID
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_MESSAGE
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_SERIAL_CODE
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_SOFTWARE_VERSION
import com.kiwi.api.payments.application.TestConstants.Companion.TERMINAL_USE_CODE
import com.kiwi.api.payments.application.TestConstants.Companion.TIP_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.TOTAL_AMOUNT
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_BATCH
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_INSTALLMENTS
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_RRN
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_TICKET
import com.kiwi.api.payments.application.TestConstants.Companion.TRANSACTION_TRACE
import com.kiwi.api.payments.application.TestConstants.Companion.WORKING_KEY
import com.kiwi.api.payments.application.aCreatedOperation
import com.kiwi.api.payments.application.anOperationRequest
import com.kiwi.api.payments.domain.field.TerminalFeature
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import java.util.UUID

class ToOperationResponseMapperSpec : FeatureSpec({
    feature("map response") {

        beforeEach { clearAllMocks() }

        val mapper = ToOperationResponseMapper(
            MaskedPanProvider()
        )

        scenario("successful operation mapping") {
            val operationRequest = anOperationRequest()
            val createdOperation = aCreatedOperation()
            val result = mapper.map(createdOperation, operationRequest)

            result shouldBe (
                OperationResponse(
                    capture = Capture(
                        card = Card(
                            holder = Holder(
                                name = IDENTIFICACION_NAME,
                                identification = Identification(
                                    number = IDENTIFICATION_NUMBER,
                                    type = IDENTIFICATION_TYPE
                                )
                            ),
                            iccData = CARD_ICC_DATA,
                            maskedPan = CARD_MASKED_PAN,
                            workingKey = WORKING_KEY,
                            bank = CARD_BANK,
                            type = CARD_TYPE,
                            brand = CARD_BRAND,
                            nationality = null
                        ),
                        inputMode = INPUT_MODE,
                        previousTransactionInputMode = PREVIOUS_TRANSACTION_INPUT_MODE
                    ),
                    amount = Amount(
                        total = TOTAL_AMOUNT,
                        currency = CURRENCY_AMOUNT,
                        breakdown = listOf(
                            Breakdown("OPERATION", OPERATION_AMOUNT),
                            Breakdown("TIP", TIP_AMOUNT)
                        )
                    ),
                    datetime = DATETIME,
                    trace = TRANSACTION_TRACE,
                    ticket = TRANSACTION_TICKET,
                    terminal = Terminal(
                        serialCode = TERMINAL_SERIAL_CODE,
                        id = UUID.fromString(TERMINAL_ID),
                        softwareVersion = TERMINAL_SOFTWARE_VERSION,
                        features = listOf(TerminalFeature.CONTACTLESS.toString())

                    ),
                    batch = TRANSACTION_BATCH,
                    installments = TRANSACTION_INSTALLMENTS,
                    authorization = Authorization(
                        code = AUTHORIZATION_CODE,
                        retrievalReferenceNumber = TRANSACTION_RRN,
                        status = Status(
                            code = "APPROVED",
                            situation = Situation(
                                id = "00",
                                description = "APPROVED"
                            )
                        )
                    ),
                    displayMessage = DisplayMessage(
                        useCode = TERMINAL_USE_CODE,
                        message = TERMINAL_MESSAGE
                    ),
                    merchant = OperationResponse.Merchant(
                        id = UUID.fromString(MERCHANT_ID)
                    )
                )
                )
        }
    }
})
