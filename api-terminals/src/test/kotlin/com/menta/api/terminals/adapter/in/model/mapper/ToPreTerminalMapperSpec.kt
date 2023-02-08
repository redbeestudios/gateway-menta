package com.menta.api.terminals.adapter.`in`.model.mapper

import com.menta.api.terminals.aPreTerminal
import com.menta.api.terminals.aTerminalRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPreTerminalMapperSpec : FeatureSpec({

    feature("map terminalRequest to preTerminal") {

        val mapper = ToPreTerminalMapper()

        scenario("successful map") {

            mapper.map(aTerminalRequest()) shouldBe aPreTerminal()
        }
    }
})
