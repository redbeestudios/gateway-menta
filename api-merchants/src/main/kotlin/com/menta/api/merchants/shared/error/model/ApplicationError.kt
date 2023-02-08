package com.menta.api.merchants.shared.error.model

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.menta.api.merchants.domain.MerchantQuery
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

        fun acquirerMerchantNotFound(acquirer: String, merchantId: UUID) =
            ApplicationError(
                message = "merchant with id $merchantId for acquirer $acquirer not found",
                code = "401",
                status = NOT_FOUND
            )

        fun merchantNotFound(merchantId: UUID) =
            ApplicationError(
                message = "merchant with id $merchantId not found",
                code = "401",
                status = NOT_FOUND
            )

        fun merchantNotFound(merchantQuery: MerchantQuery) =
            with(merchantQuery) {
                "merchant with " +
                        listOfNotNull(
                            status?.let { "status: $status" },
                            merchantId?.let { "merchantId: $merchantId" },
                            customerId?.let { "customerId: $customerId" },
                        ).joinToString(", ") +
                        " not found."
            }.let {
                ApplicationError(
                    message = it,
                    code = "401",
                    status = NOT_FOUND
                )
            }

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

        fun missingParameter(origin: MissingKotlinParameterException) =
            ApplicationError(
                code = "013",
                status = BAD_REQUEST,
                message = "field ${origin.parameter.name} must be present"
            )

        fun invalidBusinessOwner() =
            ApplicationError(
                code = "020",
                status = UNPROCESSABLE_ENTITY,
                message = "Business Owner Required"
            )

        fun invalidRepresentative() =
            ApplicationError(
                code = "021",
                status = UNPROCESSABLE_ENTITY,
                message = "Representative Required"
            )

        fun invalidLegalType() =
            ApplicationError(
                code = "022",
                status = UNPROCESSABLE_ENTITY,
                message = "Cannot be used at the same time business_owner and representative "
            )

        fun merchantExists() =
            ApplicationError(
                code = "402",
                status = UNPROCESSABLE_ENTITY,
                message = "Merchant already exists"
            )

        fun acquirerMerchantExists() =
            ApplicationError(
                code = "403",
                status = UNPROCESSABLE_ENTITY,
                message = "Acquirer Merchant already exists"
            )

        fun acquirerMerchantDoesNotExists() =
            ApplicationError(
                code = "404",
                status = UNPROCESSABLE_ENTITY,
                message = "Acquirer Merchant does not exists"
            )
    }
}
