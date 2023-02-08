package com.menta.apisecrets.application.port.`in`

import arrow.core.Either
import com.menta.apisecrets.domain.Secrets
import com.menta.apisecrets.shared.error.model.ApplicationError

interface FindSecretInPort {
    fun execute(terminalSerialCode: String): Either<ApplicationError, Secrets>
}
