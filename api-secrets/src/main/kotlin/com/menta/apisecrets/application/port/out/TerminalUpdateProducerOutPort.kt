package com.menta.apisecrets.application.port.out

import arrow.core.Either
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.shared.error.model.ApplicationError

interface TerminalUpdateProducerOutPort {
    fun produce(message: Secrets): Either<ApplicationError, Unit>
}
