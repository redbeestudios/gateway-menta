package com.kiwi.api.batchcloses.hexagonal.application

import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseRequest
import com.kiwi.api.batchcloses.hexagonal.adapter.controller.model.BatchCloseResponse
import com.kiwi.api.batchcloses.hexagonal.domain.Authorization
import com.kiwi.api.batchcloses.hexagonal.domain.BatchClose
import com.kiwi.api.batchcloses.shared.constants.OperationType.*
import com.kiwi.api.batchcloses.shared.error.model.ApiError
import com.kiwi.api.batchcloses.shared.error.model.ApiErrorResponse
import java.time.LocalDateTime
import java.time.Month.JANUARY
import java.time.OffsetDateTime
import java.time.ZoneOffset

val datetime = OffsetDateTime.of(LocalDateTime.of(2022, JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))

const val merchantId = "56789"

fun aBatchCloseRequest() =
    BatchCloseRequest(
        datetime = datetime.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime(),
        trace = "123",
        ticket = "234",
        batch = "234",
        terminal = BatchCloseRequest.Terminal(
            serialCode = "134",
            softwareVersion = "1.0.456"
        ),
        hostMessage = "5566447788",
        totals = listOf(
            BatchCloseRequest.Total(
                operationCode = PAYMENT,
                amount = 123456700,
                currency = "ARS"
            ),
            BatchCloseRequest.Total(
                operationCode = ANNULMENT,
                amount = 234500,
                currency = "ARS"
            ),
            BatchCloseRequest.Total(
                operationCode = REFUND,
                amount = 78900,
                currency = "ARS"
            )
        )
    )

fun aBatchClose() =
    BatchClose(
        id = "123-123",
        authorization = Authorization(
            status = Authorization.Status(
                code = "APPROVED"

            ),
            datetime = OffsetDateTime.now(),
            retrievalReferenceNumber = "456"
        ),
        merchant = BatchClose.Merchant(
            id = merchantId
        ),
        terminal = BatchClose.Terminal(
            id = "terminal id",
            serialCode = "134",
            softwareVersion = "1.0.456"
        ),
        trace = "123",
        ticket = "234",
        batch = "111",
        hostMessage = "",
        datetime = datetime,
        totals = listOf(
            BatchClose.Total(
                operationCode = PAYMENT,
                amount = 123456700,
                currency = "ARS"
            ),
            BatchClose.Total(
                operationCode = ANNULMENT,
                amount = 234500,
                currency = "ARS"
            ),
            BatchClose.Total(
                operationCode = REFUND,
                amount = 78900,
                currency = "ARS"
            )
        )
    )

fun aBatchCloseResponse() =
    BatchCloseResponse(
        id = "123-123",
        status = BatchCloseResponse.Status(code = "APPROVED", situation = null),
        authorization = BatchCloseResponse.Authorization(
            code = "123-12",
            retrievalReferenceNumber = "456"
        ),
        merchant = BatchCloseResponse.Merchant(
            id = merchantId
        ),
        terminal = BatchCloseResponse.Terminal(
            id = "terminal id",
            serialCode = "134",
            softwareVersion = "1.0.456"
        ),
        trace = "123",
        ticket = "234",
        batch = "111",
        hostMessage = "",
        datetime = datetime,
        totals = listOf(
            BatchCloseResponse.Total(
                operationCode = PAYMENT,
                amount = 123456700,
                currency = "ARS"
            ),
            BatchCloseResponse.Total(
                operationCode = ANNULMENT,
                amount = 234500,
                currency = "ARS"
            ),
            BatchCloseResponse.Total(
                operationCode = REFUND,
                amount = 78900,
                currency = "ARS"
            )
        )
    )

fun anAuthorization() =
    Authorization(
        authorizationCode = "123",
        retrievalReferenceNumber = "15656",
        datetime = OffsetDateTime.now(),
        status = Authorization.Status(code = "status")
    )

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = datetime,
        errors = listOf(
            ApiError(
                code = 401,
                resource = "/public/batch-closes",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )
