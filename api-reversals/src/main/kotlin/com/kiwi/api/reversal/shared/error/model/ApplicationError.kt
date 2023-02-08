package com.kiwi.api.reversal.shared.error.model

import org.springframework.http.HttpStatus

sealed class ApplicationError(
    val message: String,
    val code: String,
    val httpStatus: HttpStatus
)

class QueueProducerNotWritten : ApplicationError(code = "400", message = "No se pudo insertar el mensaje en la cola de pago creado", httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
