package com.kiwi.api.payments.shared.error.providers

import com.kiwi.api.payments.shared.error.model.ApiErrorResponse
import com.kiwi.api.payments.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import com.kiwi.api.payments.shared.error.model.QueueProducerNotWritten
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.converter.HttpMessageNotReadableException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_DATE

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
            val date = OffsetDateTime.now()
            val ex = mockk<HttpMessageNotReadableException>()
            val status = BAD_REQUEST
            val errorCode = MESSAGE_NOT_READABLE

            every { currentResourceProvider.provideUri() } returns "an uri"
            every { metadataProvider.provide() } returns mapOf("metadata" to "metadata value")

            every { ex.message } returns "a message"
            val result = provider.provideFor(status, ex, errorCode)

            result.errors shouldHaveAtLeastSize 1
            result.errors shouldContain ApiErrorResponse.ApiError(
                code = errorCode.value,
                resource = "an uri",
                message = "a message",
                metadata = mapOf("metadata" to "metadata value")
            )

            result.datetime.format(ISO_DATE) shouldBe date.format(ISO_DATE)

            verify(exactly = 1) { currentResourceProvider.provideUri() }
            verify(exactly = 1) { metadataProvider.provide() }
        }

        scenario("error response provided with ApplicationError") {
            val date = OffsetDateTime.now()
            val applicationError = QueueProducerNotWritten()

            every { currentResourceProvider.provideUri() } returns "an uri"
            every { metadataProvider.provide() } returns mapOf("metadata" to "metadata value")

            val result = provider.provideFor(applicationError)

            result.errors shouldHaveAtLeastSize 1
            result.errors shouldContain ApiErrorResponse.ApiError(
                code = applicationError.code.toInt(),
                resource = "an uri",
                message = "Could not insert the message in the created payment queue",
                metadata = mapOf("metadata" to "metadata value")
            )

            result.datetime.format(ISO_DATE) shouldBe date.format(ISO_DATE)

            verify(exactly = 1) { currentResourceProvider.provideUri() }
            verify(exactly = 1) { metadataProvider.provide() }
        }
    }
})
