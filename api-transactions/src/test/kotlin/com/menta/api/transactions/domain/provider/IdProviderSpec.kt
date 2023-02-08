package com.menta.api.transactions.domain.provider

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.util.UUID

class IdProviderSpec : FeatureSpec({

    feature("provide id") {

        val provider = IdProvider()

        scenario("id provided") {

            provider.provide() shouldNotBeSameInstanceAs UUID.randomUUID()
        }
    }
})
