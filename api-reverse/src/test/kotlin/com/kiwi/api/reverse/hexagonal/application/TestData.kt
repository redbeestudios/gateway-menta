package com.kiwi.api.reverse.hexagonal.application

import com.kiwi.api.reverse.hexagonal.adapter.controller.model.*
import com.kiwi.api.reverse.hexagonal.domain.*
import com.kiwi.api.reverse.shared.error.model.ApiError
import com.kiwi.api.reverse.shared.error.model.ApiErrorResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

const val paymentId = "1234567890"
const val id = "123-123"
const val holderName = "Aixa Halac"
const val identificationNumber = "35727828"
const val identificationType = "DNI"
const val currency = "ARS"
const val termianlSerialCode = "134"
const val termianlId = "402030"
const val merchantId = "506030"
const val totalAmount = "100.00"
const val breakdowndescription = "descripcion"
const val breakdownAmount = "1000"
const val installments = "10"
const val trace = "123"
const val ticket = "234"
const val cardPan = "333344445555"
const val cardexpirationDate = "06/22"
const val cardCVV = "234"
const val captureInputMode = "1111"
const val cardIccData = "data"
const val batch = "12434"
const val hostMessage = "5546546"
const val softwareVersion = "v1"
const val authorizationCode = "4002"
const val retrievalReferenceNumber = "100"
const val cardMaskedPan = "XXXXXXXXXXXX2030"
const val cardSequenceNumber = "20304504"
const val cardBank = "COMAFI"
const val cardType = "CREDIT"
const val cardBrand = "VISA"
const val track1 = "track1"
const val track2 = "track2"
const val STATUSAPPROVE = "APPROVED"

fun aPaymentRequest() =
    PaymentRequest(
        paymentId = paymentId,
        amount = PaymentRequest.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                PaymentRequest.Amount.Breakdown(
                    description = breakdowndescription,
                    amount = breakdownAmount
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        terminal = PaymentRequest.Terminal(
            serialCode = termianlSerialCode,
        ),
        capture = PaymentRequest.Capture(
            card = PaymentRequest.Capture.Card(
                holder = PaymentRequest.Capture.Card.Holder(
                    name = holderName,
                    identification = PaymentRequest.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                pan = cardPan,
                expirationDate = cardexpirationDate,
                cvv = cardCVV,
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand
            ),
            inputMode = captureInputMode,
        ),
        batch = batch,
        hostMessage = hostMessage,
        datetime = aDatetime()
    )

fun aReimbursementRequest() =
    ReimbursementRequest(
        paymentId = paymentId,
        amount = ReimbursementRequest.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                ReimbursementRequest.Amount.Breakdown(
                    description = breakdowndescription,
                    amount = breakdownAmount
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        terminal = ReimbursementRequest.Terminal(
            serialCode = termianlSerialCode,
        ),
        capture = ReimbursementRequest.Capture(
            card = ReimbursementRequest.Capture.Card(
                holder = ReimbursementRequest.Capture.Card.Holder(
                    name = holderName,
                    identification = ReimbursementRequest.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType,
                    )
                ),
                pan = cardPan,
                expirationDate = cardexpirationDate,
                cvv = cardCVV,
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand
            ),
            inputMode = captureInputMode,
        ),
        batch = batch,
        hostMessage = hostMessage,
        datetime = aDatetime()
    )

fun aBatchCloseRequest() =
    BatchCloseRequest(
        trace = trace,
        terminal = BatchCloseRequest.Terminal(
            serialCode = termianlSerialCode
        ),
        batch = batch,
        hostMessage = hostMessage,
        datetime = aDatetime(),
        softwareVersion = softwareVersion,
        ticket = ticket,
        total = BatchCloseRequest.Total(
            amount = totalAmount,
            currency = currency,
            operationCode = "400"
        )
    )

fun aPayment() =
    Payment(
        id = id,
        paymentId = paymentId,
        authorization = Authorization(
            status = Authorization.Status(
                code = STATUSAPPROVE

            ),
            transmissionTimestamp = LocalDateTime.now(),
            retrievalReferenceNumber = "456"
        ),
        amount = Payment.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                Payment.Amount.Breakdown(
                    description = breakdowndescription,
                    amount = breakdownAmount,
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        batch = batch,
        merchant = Payment.Merchant(
            id = merchantId,
        ),
        terminal = Payment.Terminal(
            id = termianlId,
            serialCode = termianlSerialCode
        ),
        capture = Payment.Capture(
            card = Card(
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                cvv = cardCVV,
                expirationDate = cardexpirationDate,
                pan = cardPan,
                track1 = track1,
                track2 = track2,
                holder = Card.Holder(
                    name = holderName,
                    identification = Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                )
            ),
            inputMode = captureInputMode,
        ),
        dateTime = aDatetime(),
        hostMessage = hostMessage
    )

fun anAnnulment() =
    Annulment(
        id = id,
        paymentId = paymentId,
        authorization = Authorization(
            status = Authorization.Status(
                code = STATUSAPPROVE

            ),
            transmissionTimestamp = LocalDateTime.now(),
            retrievalReferenceNumber = retrievalReferenceNumber
        ),
        amount = Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                Amount.Breakdown(
                    description = breakdowndescription,
                    amount = breakdownAmount,
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        batch = batch,
        merchant = Merchant(
            id = merchantId
        ),
        terminal = Terminal(
            id = termianlId,
            serialCode = termianlSerialCode
        ),
        capture = Capture(
            card = Card(
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                cvv = cardCVV,
                expirationDate = cardexpirationDate,
                pan = cardPan,
                track1 = track1,
                track2 = track2,
                holder = Card.Holder(
                    name = holderName,
                    identification = Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
            ),
            inputMode = captureInputMode,
        ),
        datetime = aDatetime(),
        hostMessage = hostMessage
    )

fun aRefund() =
    Refund(
        id = id,
        paymentId = paymentId,
        authorization = Authorization(
            status = Authorization.Status(
                code = STATUSAPPROVE

            ),
            transmissionTimestamp = LocalDateTime.now(),
            retrievalReferenceNumber = retrievalReferenceNumber
        ),
        amount = Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                Amount.Breakdown(
                    description = breakdowndescription,
                    amount = breakdownAmount,
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        batch = batch,
        merchant = Merchant(
            id = merchantId
        ),
        terminal = Terminal(
            id = termianlId,
            serialCode = termianlSerialCode
        ),
        capture = Capture(
            card = Card(
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                cvv = cardCVV,
                expirationDate = cardexpirationDate,
                pan = cardPan,
                track1 = track1,
                track2 = track2,
                holder = Card.Holder(
                    name = holderName,
                    identification = Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
            ),
            inputMode = captureInputMode,
        ),
        datetime = aDatetime(),
        hostMessage = hostMessage
    )

fun aBatchClose() =
    BatchClose(
        id = id,
        authorization = Authorization(
            status = Authorization.Status(
                code = STATUSAPPROVE

            ),
            transmissionTimestamp = LocalDateTime.now(),
            retrievalReferenceNumber = retrievalReferenceNumber
        ),
        trace = trace,
        batch = batch,
        merchant = BatchClose.Merchant(
            id = merchantId
        ),
        terminal = BatchClose.Terminal(
            id = termianlId,
            serialCode = termianlSerialCode
        ),
        datetime = aDatetime(),
        hostMessage = hostMessage,
        ticket = ticket,
        total = BatchClose.Total(
            amount = totalAmount,
            currency = currency,
            operationCode = "400"
        ),
        softwareVersion = softwareVersion
    )

fun aPaymentResponse() =
    PaymentResponse(
        id = id,
        paymentId = paymentId,
        status = PaymentResponse.Status(code = STATUSAPPROVE),
        authorization = PaymentResponse.Authorization(
            code = authorizationCode,
            retrievalReferenceNumber = retrievalReferenceNumber
        ),
        datetime = aDatetime(),
        amount = PaymentResponse.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                PaymentResponse.Amount.Breakdown(
                    description = breakdowndescription,
                    amount = breakdownAmount
                )
            )
        ),
        installments = installments,
        trace = trace,
        ticket = ticket,
        merchant = PaymentResponse.Merchant(
            id = merchantId,
        ),
        terminal = PaymentResponse.Terminal(
            id = termianlId,
            serialCode = termianlSerialCode,
        ),
        capture = PaymentResponse.Capture(
            inputMode = captureInputMode,
            card = PaymentResponse.Capture.Card(
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                holder = PaymentResponse.Capture.Card.Holder(
                    name = holderName,
                    identification = PaymentResponse.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                maskedPan = cardMaskedPan
            )
        ),
        hostMessage = hostMessage,
        batch = batch
    )

fun aReimbursementResponse() =
    ReimbursementResponse(
        id = id,
        paymentId = paymentId,
        status = ReimbursementResponse.Status(code = STATUSAPPROVE),
        authorization = ReimbursementResponse.Authorization(
            code = authorizationCode,
            retrievalReferenceNumber = retrievalReferenceNumber
        ),
        datetime = aDatetime(),
        amount = ReimbursementResponse.Amount(
            total = totalAmount,
            currency = currency,
            breakdown = listOf(
                ReimbursementResponse.Amount.Breakdown(
                    description = breakdowndescription,
                    amount = breakdownAmount
                )
            )
        ),
        trace = trace,
        ticket = ticket,
        merchant = ReimbursementResponse.Merchant(
            id = merchantId,
        ),
        terminal = ReimbursementResponse.Terminal(
            id = termianlId,
            serialCode = termianlSerialCode,
        ),
        capture = ReimbursementResponse.Capture(
            inputMode = captureInputMode,
            card = ReimbursementResponse.Capture.Card(
                iccData = cardIccData,
                cardSequenceNumber = cardSequenceNumber,
                bank = cardBank,
                type = cardType,
                brand = cardBrand,
                holder = ReimbursementResponse.Capture.Card.Holder(
                    name = holderName,
                    identification = ReimbursementResponse.Capture.Card.Holder.Identification(
                        number = identificationNumber,
                        type = identificationType
                    )
                ),
                maskedPan = cardMaskedPan
            )
        ),
        hostMessage = hostMessage,
        batch = batch,
        installments = installments
    )

fun aBatchCloseResponse() =
    BatchCloseResponse(
        id = id,
        status = BatchCloseResponse.Status(code = STATUSAPPROVE),
        authorization = BatchCloseResponse.Authorization(
            code = authorizationCode,
            retrievalReferenceNumber = retrievalReferenceNumber
        ),
        datetime = OffsetDateTime.of(2022, 4, 19, 11, 23, 23, 25, ZoneOffset.MAX),
        trace = trace,
        merchant = BatchCloseResponse.Merchant(
            id = merchantId,
        ),
        terminal = BatchCloseResponse.Terminal(
            id = termianlId,
            serialCode = termianlSerialCode,
        ),
        hostMessage = hostMessage,
        batch = batch,
        softwareVersion = softwareVersion,
        ticket = ticket,
        total = BatchCloseResponse.Total(
            amount = totalAmount,
            currency = currency,
            operationCode = "400"
        )
    )

fun anAuthorization() =
    Authorization(
        authorizationCode = authorizationCode,
        retrievalReferenceNumber = retrievalReferenceNumber,
        transmissionTimestamp = LocalDateTime.now(),
        status = Authorization.Status(code = STATUSAPPROVE)
    )

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = aDatetime(),
        errors = listOf(
            ApiError(
                code = 401,
                resource = "/reimbursements",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )

private fun aDatetime() =
    OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)
