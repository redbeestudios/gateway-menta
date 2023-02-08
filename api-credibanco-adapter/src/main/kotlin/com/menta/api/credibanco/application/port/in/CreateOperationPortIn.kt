package com.menta.api.credibanco.application.port.`in`

import arrow.core.Either
import com.menta.api.credibanco.domain.CreatedOperation
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.shared.error.model.ApplicationError

interface CreateOperationPortIn {
    fun execute(operation: Operation): Either<ApplicationError, CreatedOperation>
}
