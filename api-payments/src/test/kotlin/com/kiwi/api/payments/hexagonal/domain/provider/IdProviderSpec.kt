package com.kiwi.api.payments.hexagonal.domain.provider

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.string.shouldMatch

class IdProviderSpec : FeatureSpec({

    feature("provide id") {

        val provider = IdProvider()

        scenario("id provided") {

            provider.provide().toString() shouldMatch "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
        }
    }
})
