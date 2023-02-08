package com.menta.api.merchants.acquirer.adapter.`in`.model.mapper

import com.menta.api.merchants.aPreAcquirerMerchant
import com.menta.api.merchants.acquirer.aMerchantId
import com.menta.api.merchants.anAcquirerMerchantRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToPreAcquirerMerchantMapperSpec : FeatureSpec({

    val mapper = ToPreAcquirerMerchantMapper()

    feature("map AcquirerMerchantRequest to PreAcquirerMerchant") {

        scenario("successful map") {
            mapper.map(
                anAcquirerMerchantRequest(), aMerchantId
            ) shouldBe aPreAcquirerMerchant()
        }
    }
})
