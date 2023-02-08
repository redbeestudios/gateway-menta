package com.kiwi.api.payments.hexagonal.domain.provider

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class MaskPanProviderSpec : FeatureSpec({

    val provider = MaskPanProvider()

    feature("mask") {

        scenario("Mask visa card") {
            provider.provide("4517660159144665") shouldBe "XXXXXXXXXXXX4665"
        }
    }
})
