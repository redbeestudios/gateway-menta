package com.menta.api.customers.shared.error

import com.menta.api.customers.anApiErrorResponse
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.exception.ApplicationErrorException
import com.menta.api.customers.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
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
            val applicationError = ApplicationError(HttpStatus.BAD_REQUEST, "an exception", ex)

            every { provider.provideFor(applicationError) } returns errorResponse

            handler.handleMessageNotReadable(ex) shouldBe ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)

            verify(exactly = 1) { provider.provideFor(applicationError) }
        }

        scenario("an ApplicationErrorException occurs") {
            val applicationError = ApplicationError(HttpStatus.NOT_FOUND, "an exception", RuntimeException("an exception"))
            val ex = ApplicationErrorException(applicationError)
            val errorResponse = anApiErrorResponse()

            every { provider.provideFor(applicationError) } returns errorResponse

            handler.handleApplicationErrorException(ex) shouldBe ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)

            verify(exactly = 1) { provider.provideFor(applicationError) }
        }

        scenario("a generic error occurs") {
            val errorResponse = anApiErrorResponse()
            val ex = RuntimeException("an exception")
            val applicationError = ApplicationError(HttpStatus.INTERNAL_SERVER_ERROR, "an exception", ex)

            every { provider.provideFor(applicationError) } returns errorResponse

            handler.handle(ex) shouldBe ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)

            verify(exactly = 1) { provider.provideFor(applicationError) }
        }
    }
})
