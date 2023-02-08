package com.kiwi.api.reimbursements.hexagonal.application.port.out

import com.kiwi.api.reimbursements.hexagonal.domain.Terminal
import java.util.UUID

interface TerminalRepositoryPortOut {
    fun retrieve(terminalId: UUID): Terminal
}
