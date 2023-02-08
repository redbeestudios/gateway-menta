package com.kiwi.api.payments.shared.util.either

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.kiwi.api.payments.shared.error.model.ApplicationError
import java.util.Optional

fun <R> Optional<R>.rightIfPresent(error: ApplicationError): Either<ApplicationError, R> =
    if (isPresent) {
        get().right()
    } else {
        error.left()
    }
