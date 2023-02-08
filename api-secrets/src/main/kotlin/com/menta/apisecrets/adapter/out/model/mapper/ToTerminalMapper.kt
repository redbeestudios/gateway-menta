package com.menta.apisecrets.adapter.out.model.mapper

import com.menta.apisecrets.adapter.out.model.TerminalResponse
import com.menta.apisecrets.domain.Terminal
import com.menta.apisecrets.shared.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToTerminalMapper {

    fun mapFrom(response: TerminalResponse) =
        with(response) {
            Terminal(
                serialCode = serialCode,
                id = id,
                merchant = Terminal.Merchant(
                    id = merchantId,
                    customer = Terminal.Merchant.Customer(
                        id = customerId
                    )
                )
            )
        }.log { info("terminal mapped: {}", it) }

    companion object : CompanionLogger()
}
