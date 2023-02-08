package com.menta.api.terminals.shared.utils.either

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.terminals.shared.error.model.ApplicationError
import org.springframework.data.domain.Page
import java.util.Optional

fun <R> Optional<R>.rightIfPresent(error: ApplicationError): Either<ApplicationError, R> =
    if (isPresent) {
        get().right()
    } else {
        error.left()
    }

fun <R> Page<R>.rightIfPresent(error: ApplicationError): Either<ApplicationError, Page<R>> =
    if (isEmpty) {
        error.left()
    } else {
        right()
    }
