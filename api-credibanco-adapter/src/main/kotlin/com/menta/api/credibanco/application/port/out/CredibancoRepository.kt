package com.menta.api.credibanco.application.port.out

import arrow.core.Either
import com.menta.api.credibanco.domain.CreatedOperation
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.shared.error.model.ApplicationError

interface CredibancoRepository {
    fun authorize(operation: Operation): Either<ApplicationError, CreatedOperation>
}
