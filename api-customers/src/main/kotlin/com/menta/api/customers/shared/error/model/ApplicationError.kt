package com.menta.api.customers.shared.error.model

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.menta.api.customers.customer.domain.CustomerQuery
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import java.util.UUID

data class ApplicationError(
    val status: HttpStatus,
    val message: String,
    val origin: Throwable? = null
) {
    companion object {

        fun validationError(origin: Throwable, message: String?) =
            ApplicationError(
                status = BAD_REQUEST,
                message = message ?: "validation error occurred",
                origin = origin
            )

        fun invalidAcquirer(acquirerId: String) =
            ApplicationError(
                message = "invalid acquirer: $acquirerId",
                status = BAD_REQUEST
            )

        fun acquirerCustomerNotFound(customerId: UUID, acquirerId: String) =
            ApplicationError(
                message = "customer with id $customerId for acquirer $acquirerId not found",
                status = NOT_FOUND
            )

        fun customerNotFound(customerId: UUID) =
            ApplicationError(
                message = "customer with id $customerId not found",
                status = NOT_FOUND
            )

        fun customerNotFound(customerQuery: CustomerQuery) =
            with(customerQuery) {
                "customer with " + listOfNotNull(
                    id?.let { "id: $id" },
                    status?.let { "status: $status" },
                    country?.let { "country: $country" }
                ).joinToString(", ") + " not found."
            }.let {
                ApplicationError(
                    message = it,
                    status = NOT_FOUND
                )
            }

        fun serverError(origin: Throwable) =
            ApplicationError(
                message = "internal server error",
                status = INTERNAL_SERVER_ERROR,
                origin = origin
            )

        fun missingParameter(origin: MissingKotlinParameterException) =
            ApplicationError(
                status = BAD_REQUEST,
                message = "field ${origin.parameter.name} must be present"
            )

        fun unhandledException(origin: Throwable) =
            ApplicationError(
                status = INTERNAL_SERVER_ERROR,
                message = origin.message ?: "unhandled exception",
                origin = origin
            )

        fun messageNotReadable(origin: Throwable) =
            ApplicationError(
                status = BAD_REQUEST,
                message = origin.message ?: "message not readable",
                origin = origin
            )

        fun invalidBusinessOwner() =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "Business Owner Required"
            )

        fun invalidRepresentative() =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "Representative Required"
            )

        fun invalidLegalType() =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "Cannot be used at the same time business_owner and representative "
            )

        fun customerExists() =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "Customer already exists"
            )

        fun acquirerCustomerExists() =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "Acquirer Customer already exists"
            )

        fun acquirerCustomerDoesNotExists() =
            ApplicationError(
                status = UNPROCESSABLE_ENTITY,
                message = "Acquirer Customer does not exists"
            )
    }
}
