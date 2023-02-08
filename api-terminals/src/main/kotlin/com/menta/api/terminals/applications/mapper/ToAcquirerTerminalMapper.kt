package com.menta.api.terminals.applications.mapper

import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.acquirer.domain.PreAcquirerTerminal
import com.menta.api.terminals.domain.provider.IdProvider
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAcquirerTerminalMapper(
    private val idProvider: IdProvider
) {
    fun map(preAcquirerTerminal: PreAcquirerTerminal) =
        with(preAcquirerTerminal) {
            AcquirerTerminal(
                id = idProvider.provide(),
                terminalId = terminalId,
                acquirer = acquirerId,
                code = code
            )
        }.log { info("created acquirer terminal mapped: {}", it) }

    companion object : CompanionLogger()
}
