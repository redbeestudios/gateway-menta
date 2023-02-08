package com.menta.api.terminals.acquirer.adapter.`in`.model.mapper

import com.menta.api.terminals.acquirer.anAcquirerTerminal
import com.menta.api.terminals.acquirer.anAcquirerTerminalResponse
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToAcquirerTerminalResponseMapperSpec : FeatureSpec({

    feature("map terminal to response") {

        val mapper = ToAcquirerTerminalResponseMapper()

        scenario("successful map") {

            mapper.mapFrom(anAcquirerTerminal) shouldBe anAcquirerTerminalResponse
        }
    }
})
