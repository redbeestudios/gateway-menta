package com.menta.api.terminals.applications.port.`in`

import arrow.core.Either
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.acquirer.domain.PreAcquirerTerminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import java.util.Optional

interface UpdateAcquirerTerminalPortIn {
    fun execute(
        preAcquirerTerminal: PreAcquirerTerminal,
        existingAcquirerTerminal: Optional<AcquirerTerminal>
    ): Either<ApplicationError, AcquirerTerminal>
}
