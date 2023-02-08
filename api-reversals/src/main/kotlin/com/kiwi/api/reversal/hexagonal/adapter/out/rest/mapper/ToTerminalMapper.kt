package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.TerminalResponse
import com.kiwi.api.reversal.hexagonal.domain.entities.ReceivedTerminal
import org.springframework.stereotype.Component

@Component
class ToTerminalMapper {

    fun map(response: TerminalResponse): ReceivedTerminal =
        with(response) {
            ReceivedTerminal(
                id = id,
                merchantId = merchantId,
                customerId = customerId,
                serialCode = serialCode,
                hardwareVersion = hardwareVersion,
                tradeMark = tradeMark,
                model = model,
                status = status,
                features = features
            )
        }
}
