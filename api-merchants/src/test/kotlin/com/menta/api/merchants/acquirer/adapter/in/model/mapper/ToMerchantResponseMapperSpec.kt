package com.menta.api.merchants.acquirer.adapter.`in`.model.mapper

import com.menta.api.merchants.acquirer.anAcquirerMerchant
import com.menta.api.merchants.acquirer.anAcquirerMerchantResponse
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToAcquirerTerminalResponseMapperSpec : FeatureSpec({

    feature("map merchant to response") {

        val mapper = ToAcquirerMerchantResponseMapper()

        scenario("successful map") {

            mapper.mapFrom(anAcquirerMerchant) shouldBe anAcquirerMerchantResponse
        }
    }
})
