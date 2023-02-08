package com.menta.api.users.authorities.shared.other.error

import com.menta.api.users.authorities.datetime
import com.menta.api.users.authorities.shared.other.error.model.ApiError
import com.menta.api.users.authorities.shared.other.error.model.ApiErrorResponse
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError
import com.menta.api.users.authorities.shared.other.error.model.exception.ApplicationErrorException
import com.menta.api.users.authorities.shared.other.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException

class ErrorHandlerSpec : FeatureSpec({

    feature("handle errors") {

        lateinit var handler: ErrorHandler
        lateinit var provider: ErrorResponseProvider

        beforeEach {
            provider = mockk()
            handler = ErrorHandler(provider)
        }

        scenario("an HttpMessageNotReadableException occurs") {
            val ex = HttpMessageNotReadableException("an exception")
            val errorResponse = anApiErrorResponse()
            val applicationError = ApplicationError(BAD_REQUEST, "an exception", ex)

            // given mocked dependencies
            every { provider.provideFor(applicationError) } returns errorResponse

            // expect that
            handler.handleMessageNotReadable(ex) shouldBe ResponseEntity(errorResponse, BAD_REQUEST)

            // dependencies called
            verify(exactly = 1) { provider.provideFor(applicationError) }
        }

        scenario("an ApplicationErrorException occurs") {
            val applicationError = ApplicationError(NOT_FOUND, "an exception", RuntimeException("an exception"))
            val ex = ApplicationErrorException(applicationError)
            val errorResponse = anApiErrorResponse()

            // given mocked dependencies
            every { provider.provideFor(applicationError) } returns errorResponse

            // expect that
            handler.handleApplicationErrorException(ex) shouldBe ResponseEntity(errorResponse, NOT_FOUND)

            // dependencies called
            verify(exactly = 1) { provider.provideFor(applicationError) }
        }

        scenario("a generic error occurs") {
            val errorResponse = anApiErrorResponse()
            val ex = RuntimeException("an exception")
            val applicationError = ApplicationError(INTERNAL_SERVER_ERROR, "an exception", ex)

            // given mocked dependencies
            every { provider.provideFor(applicationError) } returns errorResponse

            // expect that
            handler.handle(ex) shouldBe ResponseEntity(errorResponse, INTERNAL_SERVER_ERROR)

            // dependencies called
            verify(exactly = 1) { provider.provideFor(applicationError) }
        }
    }
})

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = datetime,
        errors = listOf(
            ApiError(
                resource = "/",
                message = "this is a detail",
                metadata = mapOf("query_string" to "")
            )
        )
    )
