package com.menta.api.terminals.adapter.`in`.model.mapper

import com.menta.api.terminals.adapter.`in`.model.TerminalRequest
import com.menta.api.terminals.domain.PreTerminal
import org.springframework.stereotype.Component

@Component
class ToPreTerminalMapper {
    fun map(terminalRequest: TerminalRequest): PreTerminal =
        with(terminalRequest) {
            PreTerminal(
                merchantId = merchantId,
                customerId = customerId,
                serialCode = serialCode,
                hardwareVersion = hardwareVersion,
                tradeMark = tradeMark,
                model = model,
                features = features
            )
        }
}
