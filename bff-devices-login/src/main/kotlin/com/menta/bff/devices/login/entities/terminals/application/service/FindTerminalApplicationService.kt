package com.menta.bff.devices.login.entities.terminals.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.terminals.application.port.out.FindTerminalPortOut
import com.menta.bff.devices.login.entities.terminals.domain.Terminal
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindTerminalApplicationService(
    private val findTerminal: FindTerminalPortOut
) {

    fun findBy(serialCode: String): Either<ApplicationError, Terminal> =
        findTerminal.findBy(serialCode)
            .logRight { info("terminal found: {}", it) }

    companion object : CompanionLogger()
}
