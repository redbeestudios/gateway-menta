package com.kiwi.api.payments.application.port.out

import arrow.core.Either
import com.kiwi.api.payments.domain.CreatedOperation
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.shared.error.model.ApplicationError

interface GlobalClientRepository {
    fun authorize(operation: Operation): Either<ApplicationError, CreatedOperation>
    fun ping(): Either<ApplicationError, Unit>
}
