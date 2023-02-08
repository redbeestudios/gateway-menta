package com.menta.api.terminals.applications.port.`in`

import arrow.core.Either
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface FindTerminalPortIn {
    fun execute(terminalId: UUID): Either<ApplicationError, Terminal>
    fun findByUnivocity(serialCode: String, tradeMark: String, model: String): Optional<Terminal>
}
