package com.menta.apisecrets.shared.util.rest

import arrow.core.Either
import com.menta.apisecrets.shared.error.model.ApiErrorResponse
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.util.pairedWith
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
