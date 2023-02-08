package com.menta.api.terminals.adapter.`in`.model.mapper

import com.menta.api.terminals.adapter.`in`.model.TerminalResponse
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToTerminalResponseMapper {

    fun mapFrom(terminal: Terminal) =
        with(terminal) {
            TerminalResponse(
                id = id,
                merchantId = merchantId,
                customerId = customerId,
                serialCode = serialCode,
                hardwareVersion = hardwareVersion,
                tradeMark = tradeMark,
                model = model,
                status = status,
                deleteDate = deleteDate,
                features = features
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
