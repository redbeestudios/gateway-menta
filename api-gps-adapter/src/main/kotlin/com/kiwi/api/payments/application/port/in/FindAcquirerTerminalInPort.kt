package com.kiwi.api.payments.application.port.`in`

import com.kiwi.api.payments.domain.AcquirerTerminal
import java.util.UUID

interface FindAcquirerTerminalInPort {
    fun execute(terminalId: UUID): AcquirerTerminal
}
