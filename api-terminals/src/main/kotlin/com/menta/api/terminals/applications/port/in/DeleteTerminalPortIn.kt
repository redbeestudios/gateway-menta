package com.menta.api.terminals.applications.port.`in`

import arrow.core.Either
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError

interface DeleteTerminalPortIn {
    fun execute(terminal: Terminal): Either<ApplicationError, Terminal>
}
