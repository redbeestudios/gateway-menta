package com.menta.api.transactions.shared.error

import com.menta.api.transactions.shared.error.model.ApiErrorResponse
import com.menta.api.transactions.shared.error.model.ApplicationError
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.forbidden
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.invalidArgumentError
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.messageNotReadable
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.serverError
import com.menta.api.transactions.shared.error.model.ApplicationError.Companion.unauthorized
import com.menta.api.transactions.shared.error.providers.ErrorResponseProvider
import com.menta.api.transactions.shared.util.asResponseEntity
import com.menta.api.transactions.shared.util.log.CompanionLogger
import com.menta.api.transactions.shared.util.pairedWith
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler(
    private val errorResponseProvider: ErrorResponseProvider
) {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        doHandle { messageNotReadable(getRootException(ex)) }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException) =
        doHandle { invalidArgumentError(getRootException(ex)) }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        doHandle { forbidden(ex) }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        doHandle { serverError(ex) }

    fun handleSecurityException(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        doHandle { unauthorized(ex) }

    private fun doHandle(applicationError: () -> ApplicationError) =
        buildFor(applicationError())
            .log { info("error handled: {}", it) }

    private fun getRootException(ex: Throwable): Throwable =
        if (ex.cause == null || ex == ex.cause)
            ex
        else getRootException(ex.cause!!)

    private fun buildFor(
        error: ApplicationError
    ): ResponseEntity<ApiErrorResponse> =
        errorResponseProvider.provideFor(error)
            .pairedWith(error.status)
            .asResponseEntity()

    companion object : CompanionLogger()
}
