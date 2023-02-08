package com.menta.api.users.authorities.shared.other.error.model

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.menta.api.users.authorities.domain.UserType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.REQUEST_TIMEOUT
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
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

        fun validationError(origin: Throwable, message: String?) =
            ApplicationError(
                status = BAD_REQUEST,
                message = message ?: "validation error occurred",
                origin = origin
            )

        fun missingConfigurationForUserType(type: UserType) =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "missing configuration for user type: $type",
            )

        fun timeoutError(message: String? = null) = ApplicationError(
            message = message ?: "Time out while communicating with client",
            status = REQUEST_TIMEOUT
        )

        fun clientError(origin: WebClientResponseException) =
            ApplicationError(
                status = origin.statusCode,
                message = origin.message ?: "server error", // TODO: take body instead of message
                origin = origin
            )

        fun queueProducerNotWritten() =
            ApplicationError(
                message = "No se pudo insertar el mensaje en la cola de user authorities",
                status = INTERNAL_SERVER_ERROR
            )

        fun unhandledException(origin: Throwable) =
            ApplicationError(
                status = INTERNAL_SERVER_ERROR,
                message = origin.message ?: "unhandled exception",
                origin = origin
            )
    }
}
