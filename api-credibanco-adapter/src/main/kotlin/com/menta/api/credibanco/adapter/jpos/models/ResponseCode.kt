package com.menta.api.credibanco.adapter.jpos.models

import arrow.core.Either
import com.menta.api.credibanco.adapter.jpos.models.StatusCode.FAILED
import com.menta.api.credibanco.adapter.jpos.provider.leftIfNull
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.error.model.InvalidResponseCode
import com.menta.api.credibanco.adapter.jpos.models.StatusCode.APPROVED as APPROVED_STATUS

enum class ResponseCode(val code: String, val message: String, val statusCode: StatusCode) {

    APPROVED("00", "Approved", APPROVED_STATUS),
    APPROVED_01("01", "Contact the issuer, if it is approved perform operation Off Line", APPROVED_STATUS),
    APPROVED_02("02", "Contact the issuer, if it is approved perform operation Off Line", APPROVED_STATUS),
    INVALID_TRADE("03", "Invalid Trace", FAILED),
    PICK_UP_CARD("04", "Pick up card ", FAILED),
    TRANSACTION_DENIED("05", "Transaction Denied", FAILED),
    PICK_UP_AND_CALL("07", "Pick up card and call", FAILED),
    PARTIALLY_APPROVED("10", "Partially approved", FAILED),
    APPROVED_11("11", "Approved", FAILED),
    INVALID_TRANSACTION("12", "Invalid Transaction", FAILED),
    INVALID_AMOUNT("13", "Invalid amount", FAILED),
    INVALID_CARD("14", "Invalid card number", FAILED),
    RECORD_NOT_FOUND("25", "original doesnt exist, record not found in transaction file", FAILED),
    MESSAGE_FORMAT_ERROR("30", "Message Format error", FAILED),
    FOREIGN_CURRENCY("31", "Foreign Currency", FAILED),
    DENIED_EXCEED_RETRIES("38", "Denied, exceeds number of PIN retries allowed", FAILED),
    DENIED("43", "Denied, Pick up card.", FAILED),
    CARD_DISABLED_FOR_INSTALLMENTS("45", "Card disabled for installments", FAILED),
    CARD_NOT_VALID("46", "Card not valid", FAILED),
    PIN_REQUIRED("47", "PIN required", FAILED),
    EXCEED_INSTALLMENTS("48", "Exceeds the maximum number of installments allowed", FAILED),
    EXPIRATION_DATE_FORMAT_ERROR("49", "Error in expiration date format", FAILED),
    AMOUNT_EXCEEDS_LIMIT("50", "Entered amount exceeds limit", FAILED),
    NOT_AVAILABLE("51", "Not available", FAILED),
    NOT_EXIST_ACCOUNT("53", "Doest exist account", FAILED),
    EXPIRED_CARD("54", "expired card", FAILED),
    PIN_ERROR("55", "Incorrect PIN", FAILED),
    NOT_ENABLED_ISSUER("56", "Issuer not enabled in the system", FAILED),
    NOT_ALLOWED_TRANSACTION_CARD("57", "Transaction not allowed to this card", FAILED),
    INVALID_SERVICE("58", "Invalid service. Transaction not allowed to the terminal", FAILED),
    EXCEED_ACTIVITY_LIMIT("65", "Activity Limit Exceeded – Contact issuer", FAILED),
    REQUEST_AUTHORIZATION(
        "76",
        "Request phone authorization, if approved, upload the code obtained and leave the operation in OFFLINE",
        FAILED
    ),
    INSTALLMENTS_ERROR("77", "Error in plan/installments", FAILED),
    APPROVED_85("85", "Approved", FAILED),
    CRYPTO_ERROR("88", "crypto error", FAILED),
    INVALID_TERMINAL("89", "Invalid terminal", FAILED),
    NOT_RESPOND_ISSUER("91", "Issuer dont respond", FAILED),
    DUPLICATE_SEQUENCE_NUMBER("94", "Duplicate sequence number", FAILED),
    CLOSING_TRANSACTION_ERROR("95", "Difference in the closing of transactions", FAILED),
    SYSTEM_ERROR("96", "System error", FAILED),
    PRINT_INFORMATION("98", "They must print the information Supplied in the ISO Field 63.", FAILED);

    companion object {

        private val codes: Map<String, ResponseCode> =
            values().associateBy { it.code }

        fun from(code: String): Either<ApplicationError, ResponseCode> =
            codes[code].leftIfNull(InvalidResponseCode(code))
    }
}

enum class StatusCode {
    APPROVED, FAILED
}
