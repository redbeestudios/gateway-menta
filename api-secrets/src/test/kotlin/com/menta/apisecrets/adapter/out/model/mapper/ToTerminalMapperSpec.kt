package com.menta.apisecrets.adapter.out.model.mapper

import com.menta.apisecrets.aTerminalResponse
import com.menta.apisecrets.domain.Terminal
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToTerminalMapperSpec : FeatureSpec({

    val mapper = ToTerminalMapper()

    feature("map terminal from terminal response") {

        scenario("terminal mapped") {
            aTerminalResponse().let {
                mapper.mapFrom(it) shouldBe Terminal(
                    serialCode = it.serialCode,
                    id = it.id,
                    merchant = Terminal.Merchant(
                        id = it.merchantId,
                        customer = Terminal.Merchant.Customer(
                            id = it.customerId
                        )
                    )
                )
            }
        }
    }
})
