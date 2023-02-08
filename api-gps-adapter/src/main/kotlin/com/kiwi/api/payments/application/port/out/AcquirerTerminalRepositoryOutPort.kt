package com.kiwi.api.payments.application.port.out

import com.kiwi.api.payments.domain.AcquirerTerminal
import java.util.UUID

interface AcquirerTerminalRepositoryOutPort {
    fun findBy(terminalId: UUID): AcquirerTerminal
}
