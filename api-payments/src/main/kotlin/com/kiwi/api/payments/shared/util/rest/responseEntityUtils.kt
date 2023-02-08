package com.kiwi.api.payments.shared.util.rest

import arrow.core.Either
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.ApplicationErrorException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> Pair<T, HttpStatus>.asResponseEntity() =
    ResponseEntity(
        first, second
    )

fun <R> Either<ApplicationError, R>.throwIfLeft(): R =
    fold(
        ifLeft = { throw ApplicationErrorException(it) },
        ifRight = { it }
    )
