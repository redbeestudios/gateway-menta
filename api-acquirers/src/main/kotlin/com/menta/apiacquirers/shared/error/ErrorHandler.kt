package com.menta.apiacquirers.shared.error

import com.menta.apiacquirers.shared.error.model.ApiErrorResponse
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.invalidArgumentError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.messageNotReadable
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.missingParameter
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.serverError
import com.menta.apiacquirers.shared.error.model.exception.ApplicationErrorException
import com.menta.apiacquirers.shared.error.providers.ErrorResponseProvider
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import com.menta.apiacquirers.shared.util.pairedWith
import com.menta.apiacquirers.shared.util.rest.asResponseEntity
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler(
    private val errorResponseProvider: ErrorResponseProvider
) {

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameter(ex: MissingServletRequestParameterException): ResponseEntity<ApiErrorResponse> =
        doHandle { missingParameter(getRootException(ex)) }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ApiErrorResponse> =
        doHandle { messageNotReadable(getRootException(ex)) }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException) =
        doHandle { invalidArgumentError(getRootException(ex)) }

    @ExceptionHandler(ApplicationErrorException::class)
    fun handleApplicationErrorException(ex: ApplicationErrorException): ResponseEntity<ApiErrorResponse> =
        buildFor(ex.error)
            .log { error("application error detected: {}", ex.error) }
            .log { error("application error handled: {}", it) }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        doHandle { serverError(ex) }

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
