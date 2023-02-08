package com.menta.api.transactions.shared.error.provider

import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.shared.error.model.ApiErrorResponse
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.operationNotFound
import com.menta.api.transactions.shared.error.providers.CurrentResourceProvider
import com.menta.api.transactions.shared.error.providers.ErrorResponseMetadataProvider
import com.menta.api.transactions.shared.error.providers.ErrorResponseProvider
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
            val error = operationNotFound(UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b"), OperationType.PAYMENT)
            every { currentResourceProvider.provideUri() } returns uri
            every { metadataProvider.provide() } returns metadata
            mockkStatic(OffsetDateTime::class)
            val date = OffsetDateTime.MAX
            every { OffsetDateTime.now() } returns date

            provider.provideFor(error) shouldBe ApiErrorResponse(
                datetime = OffsetDateTime.MAX,
                errors = listOf(
                    ApiErrorResponse.ApiError(
                        code = error.code,
                        resource = uri,
                        message = error.message!!,
                        metadata = metadata
                    )
                )
            )
        }
    }
})
