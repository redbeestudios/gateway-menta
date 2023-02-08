package com.menta.api.feenicia.shared.error

import arrow.core.left
import arrow.core.right
import com.menta.api.feenicia.shared.error.model.ApiErrorResponse
import com.menta.api.feenicia.shared.error.model.ApplicationError
import com.menta.api.feenicia.shared.error.model.InvalidArgumentError
import com.menta.api.feenicia.shared.error.model.MessageNotReadable
import com.menta.api.feenicia.shared.error.model.ServerError
import com.menta.api.feenicia.shared.error.providers.ErrorResponseProvider
import com.menta.api.feenicia.shared.util.log.CompanionLogger
import com.menta.api.feenicia.shared.util.pairedWith
import com.menta.api.feenicia.shared.util.rest.asResponseEntity
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

fun <T> T?.leftIfNull(error: ApplicationError) =
    this?.right() ?: error.left()
