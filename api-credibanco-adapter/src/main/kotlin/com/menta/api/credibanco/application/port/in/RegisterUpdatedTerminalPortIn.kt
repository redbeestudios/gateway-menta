package com.menta.api.credibanco.application.port.`in`

import arrow.core.Either
import com.menta.api.credibanco.shared.error.model.ApplicationError

interface RegisterUpdatedTerminalPortIn {
    fun execute(terminalId: String): Either<ApplicationError, String>
}
