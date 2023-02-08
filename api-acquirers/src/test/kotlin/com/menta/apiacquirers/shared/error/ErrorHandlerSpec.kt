package com.menta.apiacquirers.shared.error

import com.menta.apiacquirers.shared.error.model.ApiError
import com.menta.apiacquirers.shared.error.model.ApiErrorResponse
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.invalidArgumentError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.messageNotReadable
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.missingParameter
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.serverError
import com.menta.apiacquirers.shared.error.model.exception.ApplicationErrorException
import com.menta.apiacquirers.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import java.time.OffsetDateTime

class ErrorHandlerSpec : FeatureSpec({

    val provider = mockk<ErrorResponseProvider>()
    val handler = ErrorHandler(provider)

    beforeEach { clearAllMocks() }

    feature("handle message not readable") {

        scenario("message not readable handled") {
            val httpInputMessage = mockk<HttpInputMessage>()
            val ex = HttpMessageNotReadableException("a message", httpInputMessage)
            val applicationError = messageNotReadable(ex)
            val apiErrorResponse = anApiErrorResponse()

            every { provider.provideFor(applicationError) } returns apiErrorResponse

            handler.handleMessageNotReadable(ex) shouldBe ResponseEntity(apiErrorResponse, applicationError.status)
            verify(exactly = 1) { provider.provideFor(applicationError) }
        }

        scenario("method argument not valid handled") {
            val ex = MethodArgumentNotValidException(mockk(), mockk())
            val applicationError = invalidArgumentError(ex)
            val apiErrorResponse = anApiErrorResponse()

            every { provider.provideFor(applicationError) } returns apiErrorResponse

            handler.handleMethodArgumentNotValid(ex) shouldBe ResponseEntity(apiErrorResponse, applicationError.status)
            verify(exactly = 1) { provider.provideFor(applicationError) }
        }

        scenario("handleMissingServletRequestParameter handled") {
            val ex = MissingServletRequestParameterException("parameter", "String")
            val applicationError = missingParameter(ex)
            val apiErrorResponse = anApiErrorResponse()

            every { provider.provideFor(applicationError) } returns apiErrorResponse

            handler.handleMissingServletRequestParameter(ex) shouldBe ResponseEntity(
                apiErrorResponse,
                applicationError.status
            )
            verify(exactly = 1) { provider.provideFor(applicationError) }
        }

        scenario("an ApplicationErrorException occurs") {
            val applicationError = ApplicationError(
                code = "200",
                status = HttpStatus.NOT_FOUND,
                message = "an exception",
                origin = RuntimeException("an exception")
            )
            val ex = ApplicationErrorException(applicationError)
            val errorResponse = anApiErrorResponse()

            // given mocked dependencies
            every { provider.provideFor(applicationError) } returns errorResponse

            // expect that
            handler.handleApplicationErrorException(ex) shouldBe ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)

            // dependencies called
            verify(exactly = 1) { provider.provideFor(applicationError) }
        }

        scenario("handle generic error") {
            val httpInputMessage = mockk<HttpInputMessage>()
            val ex = HttpMessageNotReadableException("a message", httpInputMessage)
            val applicationError = serverError(ex)
            val apiErrorResponse = anApiErrorResponse()

            every { provider.provideFor(applicationError) } returns apiErrorResponse

            handler.handle(ex) shouldBe ResponseEntity(apiErrorResponse, applicationError.status)
            verify(exactly = 1) { provider.provideFor(applicationError) }
        }
    }
})

fun anApiErrorResponse() =
    ApiErrorResponse(
        datetime = OffsetDateTime.MAX,
        errors = listOf(
            ApiError(
                code = "a code",
                resource = "a resource",
                message = "a message",
                metadata = emptyMap()
            )
        )
    )
