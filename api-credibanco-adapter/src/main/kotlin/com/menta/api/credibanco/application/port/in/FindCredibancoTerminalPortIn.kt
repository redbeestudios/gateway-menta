package com.menta.api.credibanco.application.port.`in`

import com.menta.api.credibanco.domain.CredibancoTerminal
import java.util.UUID

interface FindCredibancoTerminalPortIn {

    fun execute(terminalId: UUID): CredibancoTerminal
}
