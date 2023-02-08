package com.kiwi.api.payments.shared.error.model

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

sealed class ApplicationError(
    val message: String,
    val code: String,
    val httpStatus: HttpStatus
)
class QueueProducerNotWritten : ApplicationError(code = "400", message = "Could not insert the message in the created payment queue", httpStatus = INTERNAL_SERVER_ERROR)
class AcquirerError : ApplicationError(code = "400", message = "Acquirer Error", httpStatus = INTERNAL_SERVER_ERROR)
class AcquirerTimeOutError : ApplicationError(code = "400", message = "Connection timeout while authorizing at the Acquirer", httpStatus = INTERNAL_SERVER_ERROR)
class OperationNotFound : ApplicationError(code = "400", message = "Operation Not Found", httpStatus = INTERNAL_SERVER_ERROR)
