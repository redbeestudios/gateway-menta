package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.application.anAcquirerResponse
import com.kiwi.api.reversal.hexagonal.application.anAuthorization
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToAuthorizationMapperSpec : FeatureSpec({

    feature("map authorization") {

        val mapper = ToAuthorizationMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val acquirerResponse = anAcquirerResponse()
            val authorization = anAuthorization()

            mapper.map(acquirerResponse) shouldBe authorization
        }
    }
})
