package com.menta.api.terminals.acquirer.application.port.out

import arrow.core.Either
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface AcquirerTerminalRepositoryOutPort {
    fun findBy(acquirer: String, terminalId: UUID): Optional<AcquirerTerminal>
    fun create(acquirerTerminal: AcquirerTerminal): AcquirerTerminal
    fun update(acquirerTerminal: AcquirerTerminal): Either<ApplicationError, AcquirerTerminal>
}
