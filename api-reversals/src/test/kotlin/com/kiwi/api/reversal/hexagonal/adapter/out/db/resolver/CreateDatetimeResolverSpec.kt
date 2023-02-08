package com.kiwi.api.reversal.hexagonal.adapter.out.db.resolver

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import java.time.OffsetDateTime

class CreateDatetimeResolverSpec : FeatureSpec({

    val resolver = CreateDatetimeResolver()

    feature("resolve datetime") {
        scenario("successful provide") {
            resolver.provide() shouldBeLessThanOrEqualTo OffsetDateTime.now()
        }
    }
})
