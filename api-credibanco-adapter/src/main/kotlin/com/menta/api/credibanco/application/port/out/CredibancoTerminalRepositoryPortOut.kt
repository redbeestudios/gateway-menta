package com.menta.api.credibanco.application.port.out

import com.menta.api.credibanco.domain.CredibancoTerminal
import java.util.UUID

interface CredibancoTerminalRepositoryPortOut {
    fun findBy(terminalId: UUID): CredibancoTerminal
}
