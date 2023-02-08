package com.menta.bff.devices.login.shared.domain.builder

import com.menta.bff.devices.login.entities.merchant.aMerchant
import com.menta.bff.devices.login.entities.merchant.aTaxMerchant
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToMerchantBuilderSpec : FeatureSpec({

    val builder = ToMerchantBuilder()

    feature("map merchant") {
        scenario("map successfully") {
            val merchant = aMerchant().copy(taxCondition = null)
            val taxMerchant = aTaxMerchant()

            builder.buildFrom(merchant, taxMerchant) shouldBe aMerchant()
        }

        scenario("map whit taxMerchant null") {
            val merchant = aMerchant().copy(taxCondition = null)

            builder.buildFrom(merchant, null) shouldBe merchant
        }

        scenario("map whit taxMerchant null") {

            builder.buildFrom(null, null) shouldBe null
        }
    }
})
