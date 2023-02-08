package com.menta.api.customers.shared.utils.either

import arrow.core.Either
import arrow.core.rightIfNotNull
import com.menta.api.customers.shared.error.model.ApplicationError

fun <A, B> List<B>?.leftIfNullOrEmpty(default: () -> A): Either<A, List<B>> = when (this) {
    null -> Either.Left(default())
    emptyList<B>() -> Either.Left(default())
    else -> Either.Right(this)
}

fun <R> List<R>.firstOrLeft(error: ApplicationError, predicate: (R) -> Boolean): Either<ApplicationError, R> =
    firstOrNull { predicate(it) }.rightIfNotNull { error }
