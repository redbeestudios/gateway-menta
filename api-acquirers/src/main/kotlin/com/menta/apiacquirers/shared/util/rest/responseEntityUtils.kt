package com.menta.apiacquirers.shared.util.rest

import arrow.core.Either
import com.menta.apiacquirers.shared.error.model.ApiErrorResponse
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.exception.ApplicationErrorException
import com.menta.apiacquirers.shared.util.pairedWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> Pair<T, HttpStatus>.asResponseEntity() =
    ResponseEntity(
        first, second
    )

fun <R : Any> Either<ApplicationError, ResponseEntity<R>>.evaluate(
    leftAsResponse: ApplicationError.() -> ApiErrorResponse
): ResponseEntity<out Any> =
    fold(
        ifLeft = { it.leftAsResponse().pairedWith(it.status).asResponseEntity() },
        ifRight = { it }
    )

fun <R> Either<ApplicationError, R>.throwIfLeft(): R =
    fold(
        ifLeft = { throw ApplicationErrorException(it) },
        ifRight = { it }
    )
