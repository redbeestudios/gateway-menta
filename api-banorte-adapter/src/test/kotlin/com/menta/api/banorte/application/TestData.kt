package com.menta.api.banorte.application

import com.menta.api.banorte.adapter.controller.models.OperationRequest
import com.menta.api.banorte.adapter.controller.models.OperationResponse
import com.menta.api.banorte.domain.Aggregator
import com.menta.api.banorte.domain.AuthResult.APPROVED
import com.menta.api.banorte.domain.BanorteMerchant
import com.menta.api.banorte.domain.CommandTransaction.AUTH
import com.menta.api.banorte.domain.CreatedOperation
import com.menta.api.banorte.domain.EntryMode.CHIP
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.domain.User
import com.menta.api.banorte.shared.error.model.ApiErrorResponse
import com.menta.api.banorte.shared.error.model.ApiErrorResponse.ApiError
import com.menta.api.banorte.utils.TestConstants.Companion.AGGREGATOR_ID
import com.menta.api.banorte.utils.TestConstants.Companion.AMOUNT
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_BANK
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_BRAND
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_CVV
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_EXPIRATION_DATE
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_ICC_DATA
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_KSN
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_PAN
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_PIN
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_TRACK1
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_TRACK2
import com.menta.api.banorte.utils.TestConstants.Companion.CARD_TYPE
import com.menta.api.banorte.utils.TestConstants.Companion.CHILD_COMMERCE_ID
import com.menta.api.banorte.utils.TestConstants.Companion.CONTROL_NUMBER
import com.menta.api.banorte.utils.TestConstants.Companion.CURRENCY
import com.menta.api.banorte.utils.TestConstants.Companion.DATE_TIME
import com.menta.api.banorte.utils.TestConstants.Companion.DESCRIPTION_BREAKDOWN
import com.menta.api.banorte.utils.TestConstants.Companion.EMV_TAGS
import com.menta.api.banorte.utils.TestConstants.Companion.HOLDER_NAME
import com.menta.api.banorte.utils.TestConstants.Companion.INPUT_MODE
import com.menta.api.banorte.utils.TestConstants.Companion.INSTALLMENTS
import com.menta.api.banorte.utils.TestConstants.Companion.MERCHANT_ID
import com.menta.api.banorte.utils.TestConstants.Companion.REFERENCE
import com.menta.api.banorte.utils.TestConstants.Companion.RRN
import com.menta.api.banorte.utils.TestConstants.Companion.TERMINAL_ID
import com.menta.api.banorte.utils.TestConstants.Companion.TERMINAL_SERIAL_CODE
import com.menta.api.banorte.utils.TestConstants.Companion.TERMINAL_SOFTWARE_VERSION
import com.menta.api.banorte.utils.TestConstants.Companion.TRANSACTION_BATCH
import com.menta.api.banorte.utils.TestConstants.Companion.TRANSACTION_TICKET
import com.menta.api.banorte.utils.TestConstants.Companion.TRANSACTION_TRACE
import com.menta.api.banorte.utils.TestConstants.Companion.USER_NAME
import com.menta.api.banorte.utils.TestConstants.Companion.USER_PASSWORD
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

val merchantId = "4037a1d0-df4c-4ea6-844e-b37e22733cd1"

fun anOperationRequest() =
    OperationRequest(
        capture = OperationRequest.Capture(
            card = OperationRequest.Capture.Card(
                holder = OperationRequest.Capture.Card.Holder(
                    name = HOLDER_NAME,
                    identification = null
                ),
                pan = CARD_PAN,
                expirationDate = CARD_EXPIRATION_DATE,
                cvv = CARD_CVV,
                track1 = CARD_TRACK1,
                track2 = CARD_TRACK2,
                pin = CARD_PIN,
                emv = OperationRequest.Capture.Card.EMV(
                    iccData = CARD_ICC_DATA,
                    ksn = CARD_KSN
                ),
                bank = CARD_BANK,
                type = CARD_TYPE,
                brand = CARD_BRAND
            ),
            inputMode = INPUT_MODE,
            previousTransactionInputMode = null
        ),
        amount = OperationRequest.Amount(
            total = AMOUNT,
            currency = CURRENCY,
            breakdown = listOf(OperationRequest.Amount.Breakdown(description = DESCRIPTION_BREAKDOWN, amount = AMOUNT))
        ),
        datetime = DATE_TIME,
        trace = TRANSACTION_TRACE,
        ticket = TRANSACTION_TICKET,
        terminal = OperationRequest.Terminal(
            id = TERMINAL_ID,
            serialCode = TERMINAL_SERIAL_CODE,
            softwareVersion = TERMINAL_SOFTWARE_VERSION,
            features = listOf("CHIP")
        ),
        installments = INSTALLMENTS,
        merchant = OperationRequest.Merchant(
            id = MERCHANT_ID,
        ),
        batch = TRANSACTION_BATCH,
        retrievalReferenceNumber = RRN
    )

fun anOperation() =
    Operation(
        amount = AMOUNT,
        installments = INSTALLMENTS,
        aggregator = Aggregator(
            id = AGGREGATOR_ID,
            childCommerce = Aggregator.ChildCommerce(
                id = CHILD_COMMERCE_ID
            )
        ),
        card = Operation.Card(
            brand = CARD_BRAND,
            cvv = CARD_CVV,
            expirationDate = CARD_EXPIRATION_DATE,
            pan = CARD_PAN,
            track1 = null,
            track2 = null
        ),
        commandTransaction = AUTH,
        controlNumber = CONTROL_NUMBER,
        reference = REFERENCE,
        emvTags = EMV_TAGS,
        entryMode = CHIP,
        merchantId = MERCHANT_ID,
        terminal = TERMINAL_ID,
        user = User(
            name = USER_NAME,
            password = USER_PASSWORD
        ),
        url = "http://mock.tools.menta.global/InterredesSeguro",
        affiliationId = "CODE123"
    )

fun aCreatedOperation() =
    CreatedOperation(
        authResult = APPROVED,
        card = CreatedOperation.Card(
            bank = CARD_BANK,
            brand = CARD_BRAND,
            referredPan = CARD_PAN,
            type = CARD_TYPE
        ),
        controlNumber = "",
        custReqDate = OffsetDateTime.now(),
        emvData = "",
        affiliationId = "",
        paywResult = CreatedOperation.PawsResult.A,
        paywCode = "",
        referenceNumber = "",
        text = "",
        authorizationCode = "123-12",
    )

fun anOperationResponse() =
    OperationResponse(
        capture = OperationResponse.Capture(
            card = OperationResponse.Capture.Card(
                holder = OperationResponse.Capture.Card.Holder(
                    name = "sebastian",
                    identification = OperationResponse.Capture.Card.Holder.Identification(
                        number = "444444",
                        type = "DNI"
                    )
                ),
                maskedPan = "XXXXXXXXXXXX1245",
                workingKey = "1223",
                bank = "bank",
                type = "type",
                brand = CARD_BRAND,
                nationality = "ARGENTINA"
            ),
            inputMode = "45466",
            previousTransactionInputMode = "CHIP"
        ),
        amount = OperationResponse.Amount(
            total = "10000",
            currency = "ARS",
            breakdown = listOf(OperationResponse.Amount.Breakdown(description = "OPERATION", amount = "1000"))
        ),
        datetime = aDate(),
        trace = "123",
        ticket = "234",
        terminal = OperationResponse.Terminal(
            id = "1",
            serialCode = "1",
            softwareVersion = "1.0.456",
            features = listOf("ONE", "TWO", "THREE")
        ),
        batch = "12234566",
        installments = "10",
        authorization = OperationResponse.Authorization(
            code = "123-12",
            retrievalReferenceNumber = "9876543",
            status = OperationResponse.Status(
                code = "APPROVED",
                situation = OperationResponse.Status.Situation(
                    id = "302",
                    description = "This is a description"
                )
            )
        ),
        displayMessage = OperationResponse.DisplayMessage(
            useCode = "22",
            message = "Necesita Impresion"
        ),
        merchant = OperationResponse.Merchant(
            id = "123",
        )
    )

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = aDate(),
        errors = listOf(
            ApiError(
                code = "401",
                resource = "/payments",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )

fun aMerchant() = BanorteMerchant(
    id = merchantId,
    affiliationId = "CODE123",
    user = BanorteMerchant.User(
        username = "info@menta.global",
        password = "Menta2022"
    ),
    url = "https://via.banorte.com/InterredesSeguro/$merchantId"
)

private fun aDate() =
    OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)
