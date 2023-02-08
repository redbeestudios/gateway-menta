package com.kiwi.api.reimbursements.shared.error

import com.kiwi.api.reimbursements.shared.error.model.ApiErrorResponse
import com.kiwi.api.reimbursements.shared.error.model.ErrorCode
import com.kiwi.api.reimbursements.shared.error.model.ErrorCode.INTERNAL_ERROR
import com.kiwi.api.reimbursements.shared.error.model.ErrorCode.MESSAGE_NOT_READABLE
import com.kiwi.api.reimbursements.shared.error.providers.ErrorResponseProvider
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import com.kiwi.api.reimbursements.shared.util.pairedWith
import com.kiwi.api.reimbursements.shared.util.rest.asResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler(
    private val errorResponseProvider: ErrorResponseProvider
) {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        buildFor(BAD_REQUEST, getRootException(ex), MESSAGE_NOT_READABLE)
            .log { error("error handled: {}", it) }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Throwable): ResponseEntity<ApiErrorResponse> =
        buildFor(INTERNAL_SERVER_ERROR, getRootException(ex), INTERNAL_ERROR)
            .log { error("error handled: {}", it) }

    private fun buildFor(
        httpStatus: HttpStatus,
        ex: Throwable,
        errorCode: ErrorCode
    ): ResponseEntity<ApiErrorResponse> =
        errorResponseProvider.provideFor(httpStatus, ex, errorCode)
            .pairedWith(httpStatus)
            .asResponseEntity()

    private fun getRootException(ex: Throwable): Throwable =
        if (ex.cause == null || ex == ex.cause)
            ex
        else getRootException(ex.cause!!)

    companion object : CompanionLogger()
}
