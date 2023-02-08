package com.menta.api.customers.shared.error

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.menta.api.customers.shared.error.model.ApiErrorResponse
import com.menta.api.customers.shared.error.model.ApplicationError
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.messageNotReadable
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.missingParameter
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.unhandledException
import com.menta.api.customers.shared.error.model.exception.ApplicationErrorException
import com.menta.api.customers.shared.error.providers.ErrorResponseProvider
import com.menta.api.customers.shared.utils.asResponseEntity
import com.menta.api.customers.shared.utils.getRootException
import com.menta.api.customers.shared.utils.logs.CompanionLogger
import com.menta.api.customers.shared.utils.pairedWith
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
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
    fun handleMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ApiErrorResponse> =
        ex.getRootException().let {
            if (it is MissingKotlinParameterException) {
                missingParameter(it)
            } else {
                messageNotReadable(it)
            }
        }.asResponse()
            .log { error("message not readable error handled: {}", it) }

    @ExceptionHandler(ApplicationErrorException::class)
    fun handleApplicationErrorException(ex: ApplicationErrorException): ResponseEntity<ApiErrorResponse> =
        ex.error.asResponse()
            .log { error("application error detected: {}", ex.error) }
            .log { error("application error handled: {}", it) }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> =
        ex.bindingResult.allErrors.first().let {
            ApplicationError.validationError(ex, it.defaultMessage).asResponse()
        }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExceptionHandler(Exception::class)
    fun handle(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        unhandledException(ex.getRootException()).asResponse()
            .log { error("unhandled exception error handled: {}", it) }

    private fun ApplicationError.asResponse(): ResponseEntity<ApiErrorResponse> =
        errorResponseProvider.provideFor(this)
            .pairedWith(status)
            .asResponseEntity()

    companion object : CompanionLogger()
}
