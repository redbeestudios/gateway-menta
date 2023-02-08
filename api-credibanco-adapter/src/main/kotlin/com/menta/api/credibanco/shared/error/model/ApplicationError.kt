package com.menta.api.credibanco.shared.error.model

import arrow.core.Either
import com.menta.api.credibanco.adapter.jpos.models.FieldPosition
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.REQUEST_TIMEOUT
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

sealed class ApplicationError(
    val message: String,
    val code: String,
    val status: HttpStatus
)

class MessageNotReadable() : ApplicationError(code = "020", message = "El mensaje no es legible", status = BAD_REQUEST)
class ServerError() : ApplicationError(code = "010", message = "Error interno del servidor", status = INTERNAL_SERVER_ERROR)

class MissingPanError() :
    ApplicationError(message = "PAN required for Manual input mode", code = "100", status = UNPROCESSABLE_ENTITY)

class AcquirerCommunicationError :
    ApplicationError(message = "Error communicating with acquirer", code = "200", status = INTERNAL_SERVER_ERROR)

class AcquirerCommunicationTimeout :
    ApplicationError(message = "Acquirer communication timeout", code = "100", status = REQUEST_TIMEOUT)

class ExpirationInputModeError() :
    ApplicationError("expiration date must be present when input mode is MANUAL", "101", BAD_REQUEST)

class FieldExtractionError(fieldPosition: FieldPosition) :
    ApplicationError("error extracting field: $fieldPosition", "102", INTERNAL_SERVER_ERROR)

class InvalidMTI(invalidMTI: String) :
    ApplicationError("mti $invalidMTI is not valid", "103", INTERNAL_SERVER_ERROR)

class InvalidProcessCode(invalidProcessCode: String) :
    ApplicationError("processCode $invalidProcessCode is not valid", "104", INTERNAL_SERVER_ERROR)
data class InvalidTransactionType(val invalidTransactionType: String) :
    ApplicationError("transactionType $invalidTransactionType is not valid", "104", INTERNAL_SERVER_ERROR)

class InvalidAccountType(invalidAccountType: String) :
    ApplicationError("accountType $invalidAccountType is not valid", "105", INTERNAL_SERVER_ERROR)

class InvalidCardNationality(invalidCardNationality: String) :
    ApplicationError("cardNationality $invalidCardNationality is not valid", "106", INTERNAL_SERVER_ERROR)

class InvalidResponseCode(invalidResponseCode: String) :
    ApplicationError("responseCode $invalidResponseCode is not valid", "107", INTERNAL_SERVER_ERROR)

class InvalidCurrency(currency: String) :
    ApplicationError("currency $currency is not valid", "108", INTERNAL_SERVER_ERROR)

class InvalidUseCode(useCode: String) :
    ApplicationError("useCode $useCode is not valid", "109", INTERNAL_SERVER_ERROR)

class AcquirerPingError :
    ApplicationError(message = "Error ping: Transmission error", code = "300", status = NOT_FOUND)

class InvalidArgumentError(message: String) :
    ApplicationError(message, "110", BAD_REQUEST)

data class OutdatedTerminal(val serialCode: String) : ApplicationError("terminal $serialCode configuration outdated","210", UNPROCESSABLE_ENTITY)

data class TerminalAlreadyRegistered(val terminalId: String) : ApplicationError("terminal $terminalId already registered","213", UNPROCESSABLE_ENTITY)

data class NoTerminalsRegistered(val acquirer: String) : ApplicationError("no terminals registered for acquirer $acquirer","214", UNPROCESSABLE_ENTITY)

fun <R> Either<ApplicationError, R>.throwIfLeft(): R =
    fold(
        ifLeft = { throw ApplicationErrorException(it) },
        ifRight = { it }
    )

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()
