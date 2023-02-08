package com.kiwi.api.payments.shared.error.providers

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ErrorResponseMetadataProviderSpec : FeatureSpec({

    feature("provide metadata") {

        val currentResourceProvider = mockk<CurrentResourceProvider>()
        val provider = ErrorResponseMetadataProvider(currentResourceProvider)

        beforeEach {
            clearAllMocks()
        }

        feature("provide metadata") {

            scenario("metadata provided") {

                // given
                every { currentResourceProvider.provideQuery() } returns "a query"

                // expect
                provider.provide() shouldBe mapOf("query_string" to "a query")
                verify(exactly = 1) { currentResourceProvider.provideQuery() }
            }
        }
    }
})
