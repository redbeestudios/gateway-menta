package com.kiwi.api.payments.application.port.`in`

import arrow.core.Either
import com.kiwi.api.payments.domain.CreatedOperation
import com.kiwi.api.payments.domain.Operation
import com.kiwi.api.payments.shared.error.model.ApplicationError

interface CreateOperationInPort {
    fun execute(operation: Operation): Either<ApplicationError, CreatedOperation>
}
