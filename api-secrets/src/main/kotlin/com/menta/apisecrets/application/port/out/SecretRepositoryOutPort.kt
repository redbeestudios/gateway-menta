package com.menta.apisecrets.application.port.out

import arrow.core.Either
import com.menta.apisecrets.domain.Acquirer
import com.menta.apisecrets.domain.Secret
import com.menta.apisecrets.domain.Terminal
import com.menta.apisecrets.shared.error.model.ApplicationError

interface SecretRepositoryOutPort {
    fun execute(terminal: Terminal, acquirer: Acquirer): Either<ApplicationError, Secret>
}
