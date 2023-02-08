package com.menta.api.customers.customer.domain.provider

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.OffsetDateTime

class CreateDateProviderSpec : FeatureSpec({
    val provider = DateProvider()

    feature("provide creation date") {

        scenario("Ok") {
            provider.provide().shouldBeInstanceOf<OffsetDateTime>()
        }
    }
})