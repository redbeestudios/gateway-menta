package com.menta.api.seeds.shared.other.error.model

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

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

        fun forbiddenException(origin: Throwable? = null) =
            ApplicationError(
                status = HttpStatus.FORBIDDEN,
                message = "Access denied",
                origin = origin
            )

        fun unhandledException(origin: Throwable) =
            ApplicationError(
                status = INTERNAL_SERVER_ERROR,
                message = origin.message ?: "unhandled exception",
                origin = origin
            )
    }
}
