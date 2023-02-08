package com.menta.apiacquirers.shared.error.provider

import com.menta.apiacquirers.shared.error.model.ApiError
import com.menta.apiacquirers.shared.error.model.ApiErrorResponse
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.acquirerNotFound
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.serverError
import com.menta.apiacquirers.shared.error.providers.CurrentResourceProvider
import com.menta.apiacquirers.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.apiacquirers.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.time.OffsetDateTime

class ErrorResponseProviderSpec : FeatureSpec({

    feature("provide error response") {
        val currentResourceProvider = mockk<CurrentResourceProvider>()
        val metadataProvider = mockk<ErrorResponseMetadataProvider>()
        val provider = ErrorResponseProvider(
            currentResourceProvider = currentResourceProvider,
            metadataProvider = metadataProvider
        )

        beforeEach { clearAllMocks() }

        scenario("error response provided with error message") {
            val uri = "an uri"
            val metadata = mapOf("query_string" to "a query")
            val error = acquirerNotFound("a country")
            every { currentResourceProvider.provideUri() } returns uri
            every { metadataProvider.provide() } returns metadata
            mockkStatic(OffsetDateTime::class)
            val date = OffsetDateTime.MAX
            every { OffsetDateTime.now() } returns date

            provider.provideFor(error) shouldBe ApiErrorResponse(
                datetime = OffsetDateTime.MAX,
                errors = listOf(
                    ApiError(
                        code = error.code,
                        resource = uri,
                        message = error.message!!,
                        metadata = metadata
                    )
                )
            )
        }

        scenario("error response provided with origin message") {
            val uri = "an uri"
            val metadata = mapOf("query_string" to "a query")
            val error = serverError(RuntimeException("a message"))
            every { currentResourceProvider.provideUri() } returns uri
            every { metadataProvider.provide() } returns metadata
            mockkStatic(OffsetDateTime::class)
            val date = OffsetDateTime.MAX
            every { OffsetDateTime.now() } returns date

            provider.provideFor(error) shouldBe ApiErrorResponse(
                datetime = OffsetDateTime.MAX,
                errors = listOf(
                    ApiError(
                        code = error.code,
                        resource = uri,
                        message = error.origin!!.message!!,
                        metadata = metadata
                    )
                )
            )
        }
    }
})
