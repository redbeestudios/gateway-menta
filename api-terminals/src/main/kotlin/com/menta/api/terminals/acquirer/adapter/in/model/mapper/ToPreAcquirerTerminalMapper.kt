package com.menta.api.terminals.acquirer.adapter.`in`.model.mapper

import com.menta.api.terminals.acquirer.adapter.`in`.model.AcquirerTerminalRequest
import com.menta.api.terminals.acquirer.domain.PreAcquirerTerminal
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ToPreAcquirerTerminalMapper {

    fun map(acquirerTerminalRequest: AcquirerTerminalRequest, terminalId: UUID): PreAcquirerTerminal =
        with(acquirerTerminalRequest) {
            PreAcquirerTerminal(
                terminalId = terminalId,
                acquirerId = acquirerId,
                code = code
            )
        }
}
