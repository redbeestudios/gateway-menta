package com.kiwi.api.reimbursements.shared.error.model

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.REQUEST_TIMEOUT

data class ApplicationError(
    val message: String,
    val code: String,
    val status: HttpStatus
) {
    companion object {
        fun transactionNotFound(transactionId: String) = ApplicationError(message = "Transaction with id: $transactionId not found", code = "404", status = NOT_FOUND)
        fun timeout() =
            ApplicationError(message = "Time out while communicating with client", code = " 0101", status = REQUEST_TIMEOUT)
        fun transactionRepositoryError() =
            ApplicationError(message = "Error occurred while searching transaction", code = "011", status = INTERNAL_SERVER_ERROR)

        fun queueProducerNotWritten() =
            ApplicationError(code = "400", message = "No se pudo insertar el mensaje en la cola de pago creado", status = INTERNAL_SERVER_ERROR)
    }
}
