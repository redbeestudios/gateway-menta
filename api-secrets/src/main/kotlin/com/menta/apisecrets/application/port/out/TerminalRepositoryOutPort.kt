package com.menta.apisecrets.application.port.out

import arrow.core.Either
import com.menta.apisecrets.domain.Terminal
import com.menta.apisecrets.shared.error.model.ApplicationError

interface TerminalRepositoryOutPort {
    fun execute(terminalSerialCode: String): Either<ApplicationError, List<Terminal>>
}
