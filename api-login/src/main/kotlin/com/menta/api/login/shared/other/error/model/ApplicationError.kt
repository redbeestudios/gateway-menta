package com.menta.api.login.shared.other.error.model

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.menta.api.login.shared.domain.UserType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

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

        fun unauthorizedUser(origin: Throwable? = null) =
            ApplicationError(
                status = UNAUTHORIZED,
                message = "Unauthorized",
                origin = origin
            )

        fun passwordResetRequired() =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "password reset required"
            )

        fun userNotConfirmed() =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "user not confirmed"
            )

        fun authenticationProviderError(origin: Throwable) =
            ApplicationError(
                status = INTERNAL_SERVER_ERROR,
                message = "error communicating with auth provider",
                origin = origin
            )

        fun missingConfigurationForUserType(type: UserType) =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "missing configuration for user type: $type",
            )
    }
}
