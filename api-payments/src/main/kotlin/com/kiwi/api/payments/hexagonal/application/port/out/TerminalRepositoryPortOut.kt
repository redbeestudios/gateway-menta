package com.kiwi.api.payments.hexagonal.application.port.out

import com.kiwi.api.payments.hexagonal.domain.Terminal
import java.util.UUID

interface TerminalRepositoryPortOut {
    fun retrieve(terminalId: UUID): Terminal
}
