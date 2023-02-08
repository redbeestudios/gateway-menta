package com.menta.api.users.authorities.shared.other.util

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.leftIfNull
import arrow.core.right
import arrow.core.rightIfNotNull
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError

fun <A, B> List<B>?.leftIfNullOrEmpty(default: () -> A): Either<A, List<B>> = when (this) {
    null -> Either.Left(default())
    emptyList<B>() -> Either.Left(default())
    else -> Either.Right(this)
}

fun <A> A?.leftIfReceiverNull(left: ApplicationError): Either<ApplicationError, A> =
    this?.right() ?: left.left()

fun <R> Either<ApplicationError, R>?.leftIfReceiverOrRightNull(left: ApplicationError): Either<ApplicationError, R> =
    this.leftIfReceiverNull(left).flatMap { it.leftIfNull { left } }

fun <R> List<R>.firstOrLeft(error: ApplicationError): Either<ApplicationError, R> =
    firstOrNull().rightIfNotNull { error }

fun <K, V> Map<K, V>.getOrLeft(key: K, error: ApplicationError): Either<ApplicationError, V> =
    this[key].leftIfReceiverNull(error)
