package com.menta.api.terminals.shared.error

import com.menta.api.terminals.shared.error.model.ApiErrorResponse
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.invalidArgumentError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.messageNotReadable
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.serverError
import com.menta.api.terminals.shared.error.providers.ErrorResponseProvider
import com.menta.api.terminals.shared.utils.asResponseEntity
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import com.menta.api.terminals.shared.utils.pairedWith
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
        doHandle { getRootException(ex).let { messageNotReadable(it) } }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException) =
        doHandle { getRootException(ex).let { invalidArgumentError(it) } }

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
