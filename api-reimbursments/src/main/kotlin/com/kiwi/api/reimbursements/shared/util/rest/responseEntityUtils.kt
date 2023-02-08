package com.kiwi.api.reimbursements.shared.util.rest

import arrow.core.Either
import com.kiwi.api.reimbursements.shared.error.model.ApiErrorResponse
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import com.kiwi.api.reimbursements.shared.util.pairedWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> Pair<T, HttpStatus>.asResponseEntity() =
    ResponseEntity(
        first, second
    )

fun <R : Any> Either<ApplicationError, R>.evaluate(
    rightStatusCode: HttpStatus,
    leftAsResponse: ApplicationError.() -> ApiErrorResponse
): ResponseEntity<Any> =
    fold(
        ifLeft = { it.leftAsResponse().pairedWith(it.status).asResponseEntity() },
        ifRight = { it.pairedWith(rightStatusCode).asResponseEntity() }
    )
