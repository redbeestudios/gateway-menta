package com.kiwi.api.batchcloses.shared.error

import com.kiwi.api.batchcloses.shared.error.model.ErrorCode.INTERNAL_ERROR
import com.kiwi.api.batchcloses.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import com.kiwi.api.batchcloses.shared.error.providers.ErrorResponseProvider
import com.kiwi.api.batchcloses.hexagonal.application.anApiErrorResponse
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException

class ErrorHandlerSpec : FeatureSpec({

    feature("handle errors") {

        lateinit var handler: ErrorHandler
        lateinit var provider: ErrorResponseProvider

        beforeEach{
            provider = mockk()
            handler = ErrorHandler(provider)
        }

        scenario("an HttpMessageNotReadableException occurs") {

            val errorResponse = anApiErrorResponse()

            //given
            val ex = HttpMessageNotReadableException("an exception")
            every { provider.provideFor(BAD_REQUEST, ex, MESSAGE_NOT_READABLE) } returns errorResponse

            //expect
            handler.handleMessageNotReadable(ex) shouldBe ResponseEntity(errorResponse, BAD_REQUEST)
            verify(exactly = 1) { provider.provideFor(BAD_REQUEST, ex, MESSAGE_NOT_READABLE) }

        }

        scenario("a generic error occurs") {

            val errorResponse = anApiErrorResponse()

            //given
            val ex = RuntimeException("an exception")
            every { provider.provideFor(INTERNAL_SERVER_ERROR, ex, INTERNAL_ERROR) } returns errorResponse

            //expect
            handler.handle(ex) shouldBe ResponseEntity(errorResponse, INTERNAL_SERVER_ERROR)
            verify(exactly = 1) { provider.provideFor(INTERNAL_SERVER_ERROR, ex, INTERNAL_ERROR) }
        }
    }
})
