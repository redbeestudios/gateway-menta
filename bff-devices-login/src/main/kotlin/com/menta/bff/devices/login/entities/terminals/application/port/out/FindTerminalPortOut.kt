package com.menta.bff.devices.login.entities.terminals.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.terminals.domain.Terminal
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface FindTerminalPortOut {
    fun findBy(serialCode: String): Either<ApplicationError, Terminal>
}
