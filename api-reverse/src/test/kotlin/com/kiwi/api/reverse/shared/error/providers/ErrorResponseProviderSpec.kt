package com.kiwi.api.reverse.shared.error.providers

import com.kiwi.api.reverse.shared.error.model.ApiError
import com.kiwi.api.reverse.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.date.shouldHaveSameMonthAs
import io.kotest.matchers.date.shouldHaveSameYearAs
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.converter.HttpMessageNotReadableException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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
            result.errors shouldContain ApiError(
                code = errorCode.value,
                resource = "an uri",
                message = "org.springframework.http.converter.HttpMessageNotReadableException:a message",
                metadata = mapOf("metadata" to "metadata value")
            )
            result.datetime shouldHaveSameYearAs date
            result.datetime shouldHaveSameDayAs date
            result.datetime shouldHaveSameMonthAs date

            verify(exactly = 1) { currentResourceProvider.provideUri() }
            verify(exactly = 1) { metadataProvider.provide() }
        }
    }
})
