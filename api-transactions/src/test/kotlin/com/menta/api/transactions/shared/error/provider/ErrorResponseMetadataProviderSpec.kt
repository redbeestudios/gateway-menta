package com.menta.api.transactions.shared.error.provider

import com.menta.api.transactions.shared.error.providers.CurrentResourceProvider
import com.menta.api.transactions.shared.error.providers.ErrorResponseMetadataProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ErrorResponseMetadataProviderSpec : FeatureSpec({

    val currentResourceProvider = mockk<CurrentResourceProvider>()
    val provider = ErrorResponseMetadataProvider(currentResourceProvider)

    feature("provide metadata") {

        scenario("metadata provided") {
            val query = "a query"
            every { currentResourceProvider.provideQuery() } returns query

            provider.provide() shouldBe mapOf("query_string" to query)
        }
    }
})
