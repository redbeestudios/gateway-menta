package com.menta.api.terminals.acquirer.adapter.`in`.model.mapper

import com.menta.api.terminals.aPreAcquirerTerminal
import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.anAcquirerTerminalRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPreAcquirerTerminalMapperSpec : FeatureSpec({

    val mapper = ToPreAcquirerTerminalMapper()

    feature("map AcquirerTerminalRequest to PreAcquirerTerminal") {

        scenario("successful map") {
            mapper.map(
                anAcquirerTerminalRequest, aTerminalId
            ) shouldBe aPreAcquirerTerminal
        }
    }
})
