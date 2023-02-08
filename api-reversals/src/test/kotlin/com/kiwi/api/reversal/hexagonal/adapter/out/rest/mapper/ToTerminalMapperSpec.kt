package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.application.aReceivedTerminal
import com.kiwi.api.reversal.hexagonal.application.aTerminalResponse
import com.kiwi.api.reversal.hexagonal.domain.entities.ReceivedTerminal
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
    }
})
