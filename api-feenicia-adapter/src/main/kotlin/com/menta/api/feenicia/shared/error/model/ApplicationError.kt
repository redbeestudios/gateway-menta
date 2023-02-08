package com.menta.api.feenicia.shared.error.model

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

sealed class ApplicationError(
    val message: String,
    val code: String,
    val status: HttpStatus
)

class MessageNotReadable() : ApplicationError(code = "020", message = "The message is not readable", status = BAD_REQUEST)
class ServerError() : ApplicationError(code = "010", message = "Internal Server Error", status = INTERNAL_SERVER_ERROR)
class InvalidArgumentError(message: String) : ApplicationError(message, "110", BAD_REQUEST)

class FeeniciaResponseError(code: String) : ApplicationError("Invalid feenicia response", code, BAD_REQUEST)
