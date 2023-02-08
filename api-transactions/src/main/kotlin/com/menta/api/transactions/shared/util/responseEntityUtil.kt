package com.menta.api.transactions.shared.util

import arrow.core.Either
import com.menta.api.transactions.shared.error.model.ApiErrorResponse
import com.menta.api.transactions.shared.error.model.ApplicationError
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
