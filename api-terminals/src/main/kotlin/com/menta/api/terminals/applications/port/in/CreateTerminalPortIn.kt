package com.menta.api.terminals.applications.port.`in`

import arrow.core.Either
import com.menta.api.terminals.domain.PreTerminal
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import java.util.Optional

interface CreateTerminalPortIn {
    fun execute(
        preTerminal: PreTerminal,
        existingTerminal: Optional<Terminal>
    ): Either<ApplicationError, Terminal>
}
