package com.menta.api.feenicia.application

import com.menta.api.feenicia.adapter.controller.models.OperationRequest
import com.menta.api.feenicia.adapter.controller.models.OperationRequest.Capture.Card.EMV
import com.menta.api.feenicia.adapter.controller.models.OperationResponse
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Amount
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Amount.Breakdown
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Authorization
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Capture
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Capture.Card
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Capture.Card.Holder.Identification
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Merchant
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Status
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Status.Situation
import com.menta.api.feenicia.adapter.controller.models.OperationResponse.Terminal
import com.menta.api.feenicia.adapter.controller.provider.ItemsProvider.Companion.log
import com.menta.api.feenicia.adapter.rest.model.FeeniciaRequest
import com.menta.api.feenicia.adapter.rest.model.FeeniciaResponse
import com.menta.api.feenicia.adapter.rest.model.FeeniciaReverseRequest
import com.menta.api.feenicia.domain.CreatedOperation
import com.menta.api.feenicia.domain.Customer
import com.menta.api.feenicia.domain.EntryMode
import com.menta.api.feenicia.domain.FeeniciaMerchant
import com.menta.api.feenicia.domain.Operation
import com.menta.api.feenicia.domain.OperationType.PAYMENT
import com.menta.api.feenicia.domain.OperationType.REFUND
import com.menta.api.feenicia.domain.OperationType.REVERSAL
import com.menta.api.feenicia.shared.error.model.ApiErrorResponse
import com.menta.api.feenicia.shared.error.model.ApiErrorResponse.ApiError
import com.menta.api.feenicia.utils.TestConstants.Companion.AMOUNT
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_BANK
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_BRAND
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_CVV
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_EXPIRATION_DATE
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_ICC_DATA
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_KSN
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_PAN
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_PIN
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_TRACK1
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_TRACK2
import com.menta.api.feenicia.utils.TestConstants.Companion.CARD_TYPE
import com.menta.api.feenicia.utils.TestConstants.Companion.CURRENCY
import com.menta.api.feenicia.utils.TestConstants.Companion.DATE_TIME
import com.menta.api.feenicia.utils.TestConstants.Companion.DESCRIPTION_BREAKDOWN
import com.menta.api.feenicia.utils.TestConstants.Companion.HOLDER_NAME
import com.menta.api.feenicia.utils.TestConstants.Companion.INPUT_MODE
import com.menta.api.feenicia.utils.TestConstants.Companion.INSTALLMENTS
import com.menta.api.feenicia.utils.TestConstants.Companion.MERCHANT_ID
import com.menta.api.feenicia.utils.TestConstants.Companion.ORDER_ID
import com.menta.api.feenicia.utils.TestConstants.Companion.RRN
import com.menta.api.feenicia.utils.TestConstants.Companion.TERMINAL_ID
import com.menta.api.feenicia.utils.TestConstants.Companion.TERMINAL_SERIAL_CODE
import com.menta.api.feenicia.utils.TestConstants.Companion.TERMINAL_SOFTWARE_VERSION
import com.menta.api.feenicia.utils.TestConstants.Companion.TRANSACTION_BATCH
import com.menta.api.feenicia.utils.TestConstants.Companion.TRANSACTION_TICKET
import com.menta.api.feenicia.utils.TestConstants.Companion.TRANSACTION_TRACE
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

fun anOperationRequest(inputMode: String = INPUT_MODE) =
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
                emv = EMV(
                    iccData = CARD_ICC_DATA,
                    ksn = CARD_KSN
                ),
                bank = CARD_BANK,
                type = CARD_TYPE,
                brand = CARD_BRAND.name
            ),
            inputMode = inputMode,
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
            id = UUID.fromString(MERCHANT_ID),
        ),
        batch = TRANSACTION_BATCH,
        retrievalReferenceNumber = RRN,
        customer = Customer(UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"))
    )

fun aPaymentOperation() =
    Operation(
        amount = calculateAmount(),
        affiliation = "",
        cardholderName = HOLDER_NAME,
        items = listOf(
            Operation.Items(
                id = 0,
                amount = calculateAmount(),
                description = "",
                quantity = 1,
                unitPrice = calculateAmount().toString()
            ).log { info("items provided: {}", it) }
        ),
        transactionDate = 12345,
        contactless = false,
        entryMode = EntryMode.EMV,
        track2 = "5439240350653004D25022011185370500000",
        emvRequest = "ABCD",
        operationType = PAYMENT,
        deferralData = Operation.DeferralData(payments = "01"),
        feeniciaMerchant = aFeeniciaMerchant()
    )

fun aRefundOperation() = aPaymentOperation().copy(operationType = REFUND)

fun aReverseOperation() = aPaymentOperation().copy(operationType = REVERSAL, orderId = ORDER_ID)

fun aCreatedOperation() =
    CreatedOperation(
        affiliation = "",
        authnum = "",
        responseCode = "00",
        transactionId = "",
        merchant = CreatedOperation.Merchant(
            id = "",
            address = "",
            name = "",
            card = CreatedOperation.Merchant.Card(
                brand = "",
                first6Digits = "",
                last4Digits = "",
                product = ""
            )
        ),
        amount = calculateAmount(),
        tip = 0.0,
        currency = CreatedOperation.Currency(
            id = 0,
            description = ""
        ),
        issuerBank = CreatedOperation.IssuerBank(""),
        acquirerBank = CreatedOperation.AcquirerBank(""),
        approved = true,
        orderId = "",
        receiptId = ""
    )

fun calculateAmount() = AMOUNT.toDouble() / 100

fun anOperationResponse() =
    OperationResponse(
        capture = Capture(
            card = Card(
                holder = Card.Holder(
                    name = HOLDER_NAME,
                    identification = Identification(
                        type = "",
                        number = ""
                    )
                ),
                maskedPan = "",
                workingKey = "",
                bank = CARD_BANK,
                brand = CARD_BRAND.name,
                type = CARD_TYPE,
                nationality = ""
            ),
            inputMode = "",
            previousTransactionInputMode = ""
        ),
        amount = Amount(
            total = AMOUNT,
            currency = null,
            breakdown = listOf(
                Breakdown(
                    description = null,
                    amount = AMOUNT
                )
            ),
        ),
        datetime = OffsetDateTime.now(),
        trace = "",
        ticket = "",
        terminal = Terminal(
            serialCode = TERMINAL_SERIAL_CODE,
            id = TERMINAL_ID,
            softwareVersion = TERMINAL_SOFTWARE_VERSION,
            features = emptyList()
        ),
        batch = "",
        installments = null,
        authorization = Authorization(
            code = null,
            retrievalReferenceNumber = RRN,
            status = Status(
                code = null,
                situation = Situation(
                    id = "",
                    description = ""
                )
            )
        ),
        displayMessage = null,
        merchant = Merchant(
            id = UUID.fromString(MERCHANT_ID)
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

fun aFeeniciaMerchant() =
    FeeniciaMerchant(
        affiliation = "7016408",
        merchantId = "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96",
        userId = "test_menta0003",
        email = "test_menta0003@yopmail.com",
        password = "test",
        merchant = "0000000000021181",
        keys = FeeniciaMerchant.Keys(
            requestIv = "e27763f969cc76fc422e54f8a038d8ba0f6f201dcdca1bcb925599ebe4e66feb85800857cc54b94d9192f94d3055aaa2",
            requestKey = "d871459146664abcd0d372851d587fcd268654b6e277dadf7dc2c61d99bab628def2df1a0a8cba68da69ad942dbc9460",
            requestSignatureIv = "a188123343bd9d7c2423eaf7215be7a57aee5a6b3b91d23fb024369b9bef45dc89db177fd29ffcd6164c19e992c13cf8",
            requestSignatureKey = "0167ae330a32e352b18e688030284afc99b4c23931563cdd9419fc04a7da2d774dd91e505e0fc2e2a01b13a988d25031",
            responseSignatureIv = "db6e3713204aa4d0e0484b5d91e48c4d55590306c6ddacf86866e38dcc3b4411e27480bbcca1ce5aca7e7e22b7dd06b1",
            responseSignatureKey = "a3cff9c9b20f384058f71354402882be6e48bd4423c769a09852a87563ea27758d1ccd7425978d0e3f52600245eadf56",
            responseIv = "ca81c2ee7498f81552fb46710c29daf67e1908d96683ddf48d431ffc55e78495c4433ddd85626f2cc5b6c443873cbac0",
            responseKey = "32738f5b633a609b427d67ae3e33750d6a3807748fe1d055867f7298cfcd6ff41baec0a6833e30d1f2082cfcf4cdb946"
        )
    )

fun aFeeniciaResponse() = FeeniciaResponse(responseCode = "00")

fun aFeeniciaRequest() =
    FeeniciaRequest(
        "",
        4.0,
        "test",
        contactless = true,
        items = emptyList(),
        userId = "",
        track2 = "",
        transactionDate = 1L
    )

fun aFeeniciaReverseRequest() =
    FeeniciaReverseRequest(
        "",
        4.0,
        "test",
        track2 = "",
        transactionDate = 1L
    )

private fun aDate() =
    OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)
