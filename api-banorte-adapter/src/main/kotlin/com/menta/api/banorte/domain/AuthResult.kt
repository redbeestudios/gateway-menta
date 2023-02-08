package com.menta.api.banorte.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.banorte.shared.error.model.ApplicationError
import com.menta.api.banorte.shared.error.model.InvalidArgumentError

enum class AuthResult(val code: String, val message: String) {

    APPROVED("00", "Approved"),
    REFERRAL("01", "Referral / Call issuer",),
    REFER_TO_ISSUER("02", "Refer to issuer: special condition",),
    INVALID_MERCHANT("03", "Invalid merchant"),
    PICK_UP_CARD("04", "Pick up card "),
    DECLINE("05", "Declined"),
    ERROR("06", "Error"),
    RESERVED("07", "Reserved"),
    APPROVED_WITH_POSITIVE_ID("08", "Approved with positive ID",),
    NO_ACTION_TAKEN("09", "No action taken (unable to back out previous transaction)"),
    APPROVAL_VIP("11", "Approval VIP",),
    INVALID_TRANSACTION("12", "Invalid Transaction"),
    INVALID_AMOUNT("13", "Invalid amount"),
    INVALID_ACCOUNT_NUMBER("14", "Invalid account number"),
    NO_SUCH_ISSUER("15", "No such issuer"),
    SYSTEM_MALFUNCTION_1("30", "System malfunction"),
    SYSTEM_MALFUNCTION_2("31", "System malfunction"),
    EXPIRED_CARD_1("33", "Expired card"),
    DENIED_EXCEED_RETRIES("38", "Denied, exceeds number of PIN retries allowed"),
    DENIED("43", "Denied, Pick up card."),
    CARD_DISABLED_FOR_INSTALLMENTS("45", "Card disabled for installments"),
    CARD_NOT_VALID("46", "Card not valid"),
    PIN_REQUIRED("47", "PIN required"),
    EXCEED_INSTALLMENTS("48", "Exceeds the maximum number of installments allowed"),
    EXPIRATION_DATE_FORMAT_ERROR("49", "Error in expiration date format"),
    AMOUNT_EXCEEDS_LIMIT("50", "Entered amount exceeds limit"),
    NOT_AVAILABLE("51", "Not available"),
    NOT_EXIST_ACCOUNT("53", "Doest exist account"),
    EXPIRED_CARD_2("54", "expired card"),
    PIN_ERROR("55", "Incorrect PIN"),
    NOT_ENABLED_ISSUER("56", "Unable to locate record in file or account is missing"),
    NOT_ALLOWED_TRANSACTION_CARD("57", "Transaction not permitted to cardholder"),
    INVALID_SERVICE("58", "Transaction not permitted to cardholder"),
    REQUIRED_CVV("59", "Required authorization code CVV2/CVC2 was not supplied"),
    EXCEEDS_ACTIVITY_LIMIT("61", "Withdrawal amount exceeds activity limit"),
    RESTRICTED_CARD("62", "Restricted card"),
    EXCEEDS_ACTIVITY_COUNT_LIMIT("65", "Activity count limit exceeded"),
    SYSTEM_MALFUNCTION("68", "System malfunction"),
    EXCEEDS_RETRIES_PIN("75", "Allowable number of PIN entry retries exceeded"),
    SECURITY_VIOLATION("83", "Security violation"),
    NOT_AVAILABLE_HOST_1("90", "Host not available"),
    NOT_AVAILABLE_HOST_2("91", "Host not available");

    // todo:Al conectarse con la adquirencia recordar: 76,77,78,79,81,83,84,85,86,87,88,89 reserved

    companion object {

        private val codes: Map<String, AuthResult> =
            values().associateBy { it.code }

        fun from(code: String): Either<ApplicationError, AuthResult> =
            codes[code]?.right() ?: InvalidArgumentError(code).left()
    }
}
