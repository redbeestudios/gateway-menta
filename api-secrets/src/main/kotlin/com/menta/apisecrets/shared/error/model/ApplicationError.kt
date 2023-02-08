package com.menta.apisecrets.shared.error.model

import com.menta.apisecrets.domain.Country
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.REQUEST_TIMEOUT
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import java.util.UUID

data class ApplicationError(
    val message: String,
    val code: String,
    val status: HttpStatus
) {
    companion object {
        fun secretNotFound() = ApplicationError(message = "Secret not found", code = "200", status = NOT_FOUND)
        fun acquirerNotFound() = ApplicationError(message = "Acquirer not found", code = "201", status = NOT_FOUND)
        fun terminalNotFound(serialCode: String) =
            ApplicationError(message = "Terminal not found with serial code $serialCode", code = "202", status = NOT_FOUND)
        fun customerNotFound(id: UUID) =
            ApplicationError(message = "Customer not found with serial code $id", code = "203", status = NOT_FOUND)
        fun terminalRepositoryError() =
            ApplicationError(message = "Error occurred while searching terminal", code = "011", status = INTERNAL_SERVER_ERROR)
        fun timeout() =
            ApplicationError(message = "Time out while communicating with client", code = " 0101", status = REQUEST_TIMEOUT)
        fun invalidCountry(country: String) =
            ApplicationError(message = "Country must be one of: ${Country.values().map { it.toString() }}. Value: $country", code = "301", status = UNPROCESSABLE_ENTITY)
        fun queueMessageNotWritten() =
            ApplicationError(code = "400", message = "Could not insert the message in the created payment queue", status = INTERNAL_SERVER_ERROR)

    }
}
