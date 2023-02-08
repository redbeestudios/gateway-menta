package com.menta.api.customers.shared.error.provider

import com.menta.api.customers.shared.error.model.ApiErrorResponse
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.acquirerCustomerNotFound
import com.menta.api.customers.shared.error.providers.CurrentResourceProvider
import com.menta.api.customers.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.customers.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.time.OffsetDateTime
import java.util.UUID

class ErrorResponseProviderSpec : FeatureSpec({

    feature("provide error response") {
        val currentResourceProvider = mockk<CurrentResourceProvider>()
        val metadataProvider = mockk<ErrorResponseMetadataProvider>()
        val provider = ErrorResponseProvider(
            currentResourceProvider = currentResourceProvider,
            metadataProvider = metadataProvider
        )

        beforeEach { clearAllMocks() }

        scenario("error response provided") {
            val uri = "an uri"
            val metadata = mapOf("query_string" to "a query")
            val error = acquirerCustomerNotFound(UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"), "an acquirer")
            every { currentResourceProvider.provideUri() } returns uri
            every { metadataProvider.provide() } returns metadata
            mockkStatic(OffsetDateTime::class)
            val date = OffsetDateTime.MAX
            every { OffsetDateTime.now() } returns date

            provider.provideFor(error) shouldBe ApiErrorResponse(
                datetime = OffsetDateTime.MAX,
                errors = listOf(
                    ApiErrorResponse.ApiError(
                        resource = uri,
                        message = error.message!!,
                        metadata = metadata
                    )
                )
            )
        }
    }
})
