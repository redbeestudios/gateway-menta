package com.menta.api.banorte.application.port.out

import arrow.core.Either
import com.menta.api.banorte.domain.CreatedOperation
import com.menta.api.banorte.domain.Operation
import com.menta.api.banorte.shared.error.model.ApplicationError

interface BanorteClientRepository {
    fun authorize(operation: Operation): Either<ApplicationError, CreatedOperation>
}
