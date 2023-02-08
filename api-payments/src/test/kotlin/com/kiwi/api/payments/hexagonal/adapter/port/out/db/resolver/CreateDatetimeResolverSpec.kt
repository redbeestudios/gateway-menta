package com.kiwi.api.payments.hexagonal.adapter.port.out.db.resolver

import com.kiwi.api.payments.hexagonal.adapter.out.db.resolver.CreateDatetimeResolver
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import java.time.OffsetDateTime

class CreateDatetimeResolverSpec : FeatureSpec({

    val resolver = CreateDatetimeResolver()

    feature("resolve datetime") {
        scenario("successfull provide") {
            resolver.provide() shouldBeLessThanOrEqualTo OffsetDateTime.now()
        }
    }
})
