package com.menta.api.terminals.acquirer.adapter.`in`.model.mapper

import com.menta.api.terminals.acquirer.adapter.`in`.model.AcquirerTerminalResponse
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAcquirerTerminalResponseMapper {

    fun mapFrom(acquirerTerminal: AcquirerTerminal) =
        with(acquirerTerminal) {
            AcquirerTerminalResponse(
                terminalId = terminalId,
                acquirer = acquirer,
                code = code
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
