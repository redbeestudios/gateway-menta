package com.kiwi.api.payments.shared.error

import com.kiwi.api.payments.shared.error.model.ApiErrorResponse
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.InvalidArgumentError
import com.kiwi.api.payments.shared.error.model.MessageNotReadable
import com.kiwi.api.payments.shared.error.model.ServerError
import com.kiwi.api.payments.shared.error.providers.ErrorResponseProvider
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.pairedWith
import com.kiwi.api.payments.shared.util.rest.asResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler(
    private val errorResponseProvider: ErrorResponseProvider
) {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        buildFor(MessageNotReadable(), ex)
            .log { error("error handled: {}", it) }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException) = ex
        .also { log.error(HttpStatus.BAD_REQUEST.reasonPhrase, it) }
        .let {
            val message: String = it.bindingResult.allErrors.joinToString { error -> "${error.defaultMessage}" }
            buildFor(InvalidArgumentError(message))
        }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        buildFor(ServerError(), ex)
            .log { error("error handled: {}", it) }

    private fun buildFor(
        error: ApplicationError,
        ex: Throwable? = null
    ): ResponseEntity<ApiErrorResponse> =
        errorResponseProvider.provideFor(error, ex)
            .pairedWith(error.status)
            .asResponseEntity()

    companion object : CompanionLogger()
}
