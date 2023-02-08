package com.kiwi.api.payments.hexagonal.application.port.`in`

import com.kiwi.api.payments.hexagonal.domain.Terminal
import java.util.UUID

interface FindTerminalPortIn {
    fun execute(terminalId: UUID): Terminal
}
