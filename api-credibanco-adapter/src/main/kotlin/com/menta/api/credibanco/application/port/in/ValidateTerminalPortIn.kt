package com.menta.api.credibanco.application.port.`in`

import arrow.core.Either
import com.menta.api.credibanco.domain.Terminal
import com.menta.api.credibanco.shared.error.model.ApplicationError

interface ValidateTerminalPortIn {
    fun execute(terminal: Terminal): Either<ApplicationError, Boolean>
}
