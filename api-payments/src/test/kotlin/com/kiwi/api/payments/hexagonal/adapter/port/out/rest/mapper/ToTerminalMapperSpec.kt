package com.kiwi.api.payments.hexagonal.adapter.port.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToTerminalMapper
import com.kiwi.api.payments.hexagonal.application.aReceivedTerminal
import com.kiwi.api.payments.hexagonal.application.aTerminalResponse
import com.kiwi.api.payments.hexagonal.domain.Terminal
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToTerminalMapperSpec : FeatureSpec({

    feature("map terminal") {

        val mapper = ToTerminalMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val terminalResponse = aTerminalResponse()
            val terminal = aReceivedTerminal()

            mapper.map(terminalResponse) shouldBe with(terminal) {
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
    }
})
