package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.application.aTerminal
import com.kiwi.api.reimbursements.hexagonal.application.aTerminalResponse
import com.kiwi.api.reimbursements.hexagonal.domain.Terminal
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToTerminalMapperSpec : FeatureSpec({

    feature("map terminal") {

        val mapper = ToTerminalMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val terminalResponse = aTerminalResponse()
            val terminal = aTerminal()

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
