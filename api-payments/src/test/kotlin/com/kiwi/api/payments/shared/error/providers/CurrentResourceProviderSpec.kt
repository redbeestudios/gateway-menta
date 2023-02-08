package com.kiwi.api.payments.shared.error.providers

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javax.servlet.http.HttpServletRequest

class CurrentResourceProviderSpec : FeatureSpec({

    feature("provide resource") {

        lateinit var provider: CurrentResourceProvider
        lateinit var httpServletRequest: HttpServletRequest

        beforeEach {
            httpServletRequest = mockk()
            provider = CurrentResourceProvider(httpServletRequest)
        }

        scenario("provide uri") {

            // given
            every { httpServletRequest.requestURI } returns "an uri"

            // expect
            provider.provideUri() shouldBe "an uri"

            verify(exactly = 1) { httpServletRequest.requestURI }
        }

        scenario("provide existing query") {

            // given
            every { httpServletRequest.queryString } returns "a query"

            provider.provideQuery() shouldBe "a query"

            verify(exactly = 1) { httpServletRequest.queryString }
        }

        scenario("provide non existent query") {

            // given
            every { httpServletRequest.queryString } returns null

            provider.provideQuery() shouldBe ""

            verify(exactly = 1) { httpServletRequest.queryString }
        }
    }
})
