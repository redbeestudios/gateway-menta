package com.kiwi.api.payments.hexagonal.adapter.port.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToAuthorizationMapper
import com.kiwi.api.payments.hexagonal.application.anAcquirerResponse
import com.kiwi.api.payments.hexagonal.application.anAuthorization
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
