package com.menta.api.banorte.application.port.`in`

import arrow.core.Either
import com.menta.api.banorte.domain.CreatedOperation
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.shared.error.model.ApplicationError

interface CreateOperationInPort {
    fun execute(operation: Operation): Either<ApplicationError, CreatedOperation>
}
