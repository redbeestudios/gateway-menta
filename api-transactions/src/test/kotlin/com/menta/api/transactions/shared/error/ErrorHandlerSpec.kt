package com.menta.api.transactions.shared.error

import com.menta.api.transactions.anApiErrorResponse
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.invalidArgumentError
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.messageNotReadable
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.serverError
import com.menta.api.transactions.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException

class ErrorHandlerSpec : FeatureSpec({

    val provider = mockk<ErrorResponseProvider>()
    val errorHandler = ErrorHandler(provider)

    beforeEach { clearAllMocks() }

    feature("handle message not readable") {
        scenario("handle") {
            val ex = mockk<HttpMessageNotReadableException>()
            val applicationError = messageNotReadable(ex)
            val response = anApiErrorResponse()

            every { provider.provideFor(applicationError) } returns response
            every { ex.cause } returns ex

            errorHandler.handleMessageNotReadable(ex) shouldBe ResponseEntity(response, applicationError.status)
        }
    }

    feature("handle method argument not valid exception") {
        scenario("handle") {
            val ex = mockk<MethodArgumentNotValidException>()
            val applicationError = invalidArgumentError(ex)
            val response = anApiErrorResponse()

            every { provider.provideFor(applicationError) } returns response
            every { ex.cause } returns ex

            errorHandler.handle(ex) shouldBe ResponseEntity(response, applicationError.status)
        }
    }

    feature("handle generic error") {
        scenario("handle") {
            val ex = mockk<Throwable>()
            val applicationError = serverError(ex)
            val response = anApiErrorResponse()

            every { provider.provideFor(applicationError) } returns response

            errorHandler.handle(ex) shouldBe ResponseEntity(response, applicationError.status)
        }
    }
})
