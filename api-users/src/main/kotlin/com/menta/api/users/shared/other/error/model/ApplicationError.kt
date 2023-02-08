package com.menta.api.users.shared.other.error.model

import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
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

        fun validationError(origin: Throwable, message: String?) =
            ApplicationError(
                status = BAD_REQUEST,
                message = message ?: "validation error occurred",
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

        fun forbiddenException(origin: Throwable? = null) =
            ApplicationError(
                status = FORBIDDEN,
                message = "Access denied",
                origin = origin
            )

        fun userNotFound(email: Email, origin: Throwable? = null) =
            ApplicationError(
                status = NOT_FOUND,
                message = "user $email not found",
                origin = origin
            )

        fun resourceNotFound(origin: ResourceNotFoundException? = null) =
            ApplicationError(
                status = NOT_FOUND,
                message = origin?.errorMessage ?: "resource not found",
                origin = origin
            )

        fun userDisabledError(email: Email) =
            ApplicationError(
                status = NOT_FOUND,
                message = "user $email not found"
            )

        fun missingConfigurationForUserType(type: UserType) =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "missing configuration for user type: $type",
            )

        fun userAlreadyExistsError(email: Email, origin: Throwable? = null) =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "user $email already exists",
                origin = origin
            )

        fun queueProducerNotWritten(topic: String) =
            ApplicationError(
                status = INTERNAL_SERVER_ERROR,
                message = "Could not insert the message in the $topic topic"
            )
    }
}
