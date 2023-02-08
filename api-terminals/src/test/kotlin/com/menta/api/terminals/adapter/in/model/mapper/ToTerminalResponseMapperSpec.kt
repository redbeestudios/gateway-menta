package com.menta.api.terminals.adapter.`in`.model.mapper

import com.menta.api.terminals.aTerminal
import com.menta.api.terminals.aTerminalResponse
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToTerminalResponseMapperSpec : FeatureSpec({

    feature("map terminal to response") {

        val mapper = ToTerminalResponseMapper()

        scenario("successful map") {

            mapper.mapFrom(aTerminal) shouldBe aTerminalResponse
        }
    }
})
