package com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.TerminalResponse
import com.kiwi.api.payments.hexagonal.domain.Terminal
import org.springframework.stereotype.Component

@Component
class ToTerminalMapper {

    fun map(response: TerminalResponse): Terminal =
        with(response) {
            Terminal(
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
