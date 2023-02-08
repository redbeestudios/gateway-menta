package com.menta.api.terminals.acquirer.application.port.`in`

import arrow.core.Either
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface FindAcquirerTerminalPortIn {
    fun execute(acquirer: String, terminalId: UUID): Either<ApplicationError, AcquirerTerminal>
    fun find(terminalId: UUID, acquirerId: String): Optional<AcquirerTerminal>
}
