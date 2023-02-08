package com.menta.api.terminals.shared.error.model

import com.menta.api.terminals.domain.Status
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import java.util.UUID

data class ApplicationError(
    val code: String,
    val status: HttpStatus,
    val message: String? = null,
    val origin: Throwable? = null
) {
    companion object {

        fun invalidAcquirer(acquirer: String) =
            ApplicationError(
                message = "invalid acquirer: $acquirer",
                code = "402",
                status = BAD_REQUEST
            )

        fun acquirerTerminalNotFound(acquirer: String, terminalId: UUID) =
            ApplicationError(
                message = "terminal with id $terminalId for acquirer $acquirer not found",
                code = "401",
                status = NOT_FOUND
            )

        fun terminalNotFound(terminalId: UUID) =
            ApplicationError(
                message = "terminal with id $terminalId not found",
                code = "401",
                status = NOT_FOUND
            )

        fun terminalNotFound(serialCode: String) =
            ApplicationError(
                message = "terminal with serial code $serialCode not found",
                code = "401",
                status = NOT_FOUND
            )

        fun terminalNotFound(
            serialCode: String?,
            merchantId: UUID?,
            customerId: UUID?,
            terminalId: UUID?,
            status: Status?
        ) = ApplicationError(
            message = "terminal with serial code: $serialCode, merchantId: $merchantId, customerId $customerId, terminalId $terminalId not found and status $status",
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

        fun terminalExists() =
            ApplicationError(
                code = "402",
                status = UNPROCESSABLE_ENTITY,
                message = "Terminal already exists"
            )

        fun acquirerTerminalExists() =
            ApplicationError(
                code = "403",
                status = UNPROCESSABLE_ENTITY,
                message = "Acquirer Terminal already exists"
            )

        fun acquirerTerminalDoesNotExists() =
            ApplicationError(
                code = "404",
                status = UNPROCESSABLE_ENTITY,
                message = "Acquirer Terminal does not exist"
            )
    }
}
