package com.menta.api.transactions.shared.error.model

import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.TransactionType
import java.time.OffsetDateTime
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

data class ApplicationError(
    val code: String,
    val status: HttpStatus,
    val message: String? = null,
    val origin: Throwable? = null
) {
    companion object {

        fun operationNotFound(
            operationType: OperationType?,
            transactionType: TransactionType?,
            transactionId: UUID?,
            merchantId: UUID?,
            customerId: UUID?,
            terminalId: UUID?,
            status: List<StatusCode>?,
            start: OffsetDateTime,
            end: OffsetDateTime
        ) =
            "Operation with ".let { message ->
                message +
                        ifNotNull(operationType) { "operationType: $it " } +
                        ifNotNull(transactionType) { "transactionType: $it " } +
                        ifNotNull(transactionId) { "transactionId: $it " } +
                        ifNotNull(merchantId) { "merchantId: $it " } +
                        ifNotNull(customerId) { "customerId: $it " } +
                        ifNotNull(terminalId) { "terminalId: $it " } +
                        ifNotNull(status) { "status: $it " }
            }.let {
                ApplicationError(
                    message = it + "between start $start and end $end not found",
                    code = "401",
                    status = NOT_FOUND
                )
            }

        fun operationNotFound(operationId: UUID, operationType: OperationType) =
            ApplicationError(
                message = "operation with id $operationId of type ${operationType.name} not found",
                code = "401",
                status = NOT_FOUND
            )

        fun operationNotFound(acquirerId: String, operationType: OperationType) =
            ApplicationError(
                message = "operation for acquirer id $acquirerId of type ${operationType.name} not found",
                code = "401",
                status = NOT_FOUND
            )

        fun serverError(origin: Throwable) =
            ApplicationError(
                code = "010",
                message = "internal server error",
                status = INTERNAL_SERVER_ERROR,
                origin = origin
            )

        fun invalidArgumentError(origin: Throwable) =
            ApplicationError(
                code = "011",
                status = BAD_REQUEST,
                origin = origin
            )

        fun messageNotReadable(origin: Throwable) =
            ApplicationError(
                code = "012",
                status = BAD_REQUEST,
                origin = origin
            )

        fun forbidden(origin: Throwable) =
            ApplicationError(
                code = "013",
                status = FORBIDDEN,
                origin = origin
            )

        fun unauthorized(origin: Throwable) =
            ApplicationError(
                code = "014",
                status = UNAUTHORIZED,
                origin = origin
            )
    }
}

inline fun <T> ifNotNull(fromObject: T?, transform: (T) -> String): String =
    fromObject?.let { transform(fromObject) } ?: ""