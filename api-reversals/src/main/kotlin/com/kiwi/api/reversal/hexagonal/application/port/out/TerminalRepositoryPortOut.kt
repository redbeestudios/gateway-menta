package com.kiwi.api.reversal.hexagonal.application.port.out

import com.kiwi.api.reversal.hexagonal.domain.entities.ReceivedTerminal
import java.util.UUID

interface TerminalRepositoryPortOut {
    fun retrieve(terminalId: UUID): ReceivedTerminal
}
