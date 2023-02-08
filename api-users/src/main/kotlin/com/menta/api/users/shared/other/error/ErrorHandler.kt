package com.menta.api.users.shared.other.error

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.menta.api.users.shared.other.error.model.ApiErrorResponse
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.forbiddenException
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.messageNotReadable
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.missingParameter
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unhandledException
import com.menta.api.users.shared.other.error.model.exception.ApplicationErrorException
import com.menta.api.users.shared.other.error.providers.ErrorResponseProvider
import com.menta.api.users.shared.other.util.getRootException
import com.menta.api.users.shared.other.util.log.CompanionLogger
import com.menta.api.users.shared.other.util.pairedWith
import com.menta.api.users.shared.other.util.rest.asResponseEntity
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
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

    @ExceptionHandler(AccessDeniedException::class)
    fun handleSecurityException(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        forbiddenException(ex.getRootException()).asResponse()

    @Order(LOWEST_PRECEDENCE)
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
