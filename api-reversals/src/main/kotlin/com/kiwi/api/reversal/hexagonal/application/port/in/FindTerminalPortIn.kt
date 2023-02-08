package com.kiwi.api.reversal.hexagonal.application.port.`in`

import com.kiwi.api.reversal.hexagonal.domain.entities.ReceivedTerminal
import java.util.UUID

interface FindTerminalPortIn {
    fun execute(terminalId: UUID): ReceivedTerminal
}
