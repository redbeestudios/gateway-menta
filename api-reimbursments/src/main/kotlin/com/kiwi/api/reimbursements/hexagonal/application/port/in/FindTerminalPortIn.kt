package com.kiwi.api.reimbursements.hexagonal.application.port.`in`

import com.kiwi.api.reimbursements.hexagonal.domain.Terminal
import java.util.UUID

interface FindTerminalPortIn {
    fun execute(terminalId: UUID): Terminal
}
