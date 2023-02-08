package com.menta.api.taxesEntities.shared.error.model

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import java.util.UUID

data class ApplicationError(
    val code: String,
    val status: HttpStatus,
    val message: String? = null,
    val origin: Throwable? = null
) {
    companion object {

        fun notFound(merchantId: UUID) =
            ApplicationError(
                message = "tax merchant with id $merchantId not found",
                code = "401",
                status = HttpStatus.NOT_FOUND
            )

        fun feeRuleNotFound(id: UUID) =
            ApplicationError(
                message = "Rule with id $id not found",
                code = "404",
                status = HttpStatus.NOT_FOUND
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
            ApplicationError(code = "012", status = BAD_REQUEST, origin = origin)

        fun taxMerchantDoesNotExists() =
            ApplicationError(
                code = "301",
                status = UNPROCESSABLE_ENTITY,
                message = "Tax merchant does not exists"
            )

        fun taxCustomerDoesNotExists() =
            ApplicationError(
                code = "301",
                status = UNPROCESSABLE_ENTITY,
                message = "Tax customer does not exists"
            )
    }
}
