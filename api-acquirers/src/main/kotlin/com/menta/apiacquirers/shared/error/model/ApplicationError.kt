package com.menta.apiacquirers.shared.error.model

import com.menta.apiacquirers.domain.OperationType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.UUID

data class ApplicationError(
    val code: String,
    val status: HttpStatus,
    val message: String? = null,
    val origin: Throwable? = null
) {
    companion object {

        fun missingParameter(origin: Throwable) =
            ApplicationError(code = "012", status = BAD_REQUEST, message = origin.message)

        fun messageNotReadable(origin: Throwable) =
            ApplicationError(code = "012", status = BAD_REQUEST, origin = origin)

        fun serverError(origin: Throwable) =
            ApplicationError(code = "010", status = INTERNAL_SERVER_ERROR, origin = origin)

        fun invalidArgumentError(origin: Throwable) =
            ApplicationError(code = "011", status = BAD_REQUEST, origin = origin)

        fun unknownRepositoryError() =
            ApplicationError("012", status = INTERNAL_SERVER_ERROR, message = "Error communicating with repository")

        fun acquirerNotFound(country: String) =
            ApplicationError(message = "Acquirer not found for country $country", code = "200", status = NOT_FOUND)

        fun missingPathForOperation(operationType: OperationType) =
            ApplicationError(message = "Missing path for $operationType", code = "210", status = UNPROCESSABLE_ENTITY)

        fun missingPathForAcquirer(acquirer: String) =
            ApplicationError(message = "Missing path for $acquirer", code = "210", status = UNPROCESSABLE_ENTITY)

        fun timeoutError() =
            ApplicationError(
                message = "Time out while communicating with client",
                code = " 0101",
                status = HttpStatus.REQUEST_TIMEOUT
            )

        fun customerNotFound(id: UUID) =
            ApplicationError(message = "Customer not found for id $id", code = "200", status = NOT_FOUND)

        fun acquirerCustomerNotFound(customerId: UUID, acquirer: String) =
            ApplicationError(
                message = "Customer not found for id $customerId and acquirer $acquirer",
                code = "200",
                status = NOT_FOUND
            )

        fun clientError(origin: WebClientResponseException) =
            ApplicationError(
                code = "200",
                status = origin.statusCode,
                message = origin.message ?: "server error",
                origin = origin
            )
    }
}
