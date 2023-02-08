package com.menta.bff.devices.login.shared.other.error.model

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.REQUEST_TIMEOUT
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.reactive.function.client.WebClientResponseException

data class ApplicationError(
    val status: HttpStatus,
    val message: String,
    val origin: Throwable? = null
) {
    companion object {

        fun missingParameter(origin: MissingKotlinParameterException) =
            ApplicationError(
                status = BAD_REQUEST,
                message = "field ${origin.parameter.name} must be present"
            )

        fun messageNotReadable(origin: Throwable) =
            ApplicationError(
                status = BAD_REQUEST,
                message = origin.message ?: "message not readable",
                origin = origin
            )

        fun unhandledException(origin: Throwable) =
            ApplicationError(
                status = INTERNAL_SERVER_ERROR,
                message = origin.message ?: "unhandled exception",
                origin = origin
            )

        fun notFound(message: String, origin: Throwable? = null) =
            ApplicationError(
                status = NOT_FOUND,
                message = message,
                origin = origin
            )

        fun revokeTokenError(origin: Throwable? = null) =
            ApplicationError(
                status = BAD_REQUEST,
                message = "error revoking refresh token",
                origin = origin
            )

        fun unauthorizedUser(origin: Throwable? = null) =
            ApplicationError(
                status = UNAUTHORIZED,
                message = "Unauthorized",
                origin = origin
            )

        fun clientError(origin: WebClientResponseException) =
            ApplicationError(
                status = origin.statusCode,
                message = origin.message ?: "server error", // TODO: take body instead of message
                origin = origin
            )

        fun timeoutError(message: String? = null) = ApplicationError(
            message = message ?: "Time out while communicating with client",
            status = REQUEST_TIMEOUT
        )
    }
}
