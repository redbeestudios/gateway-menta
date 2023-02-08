package com.menta.api.merchants.shared.utils

import arrow.core.Either
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.exception.ApplicationErrorException
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
