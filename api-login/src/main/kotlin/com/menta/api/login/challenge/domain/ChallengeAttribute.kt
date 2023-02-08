package com.menta.api.login.challenge.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.menta.api.login.challenge.domain.ChallengeAttribute.values


enum class ChallengeAttribute {
    USERNAME,
    SMS_MFA_CODE,
    PASSWORD_CLAIM_SIGNATURE,
    PASSWORD_CLAIM_SECRET_BLOCK,
    TIMESTAMP,
    DEVICE_KEY,
    NEW_PASSWORD,
    SECRET_HASH,
    SOFTWARE_TOKEN_MFA_CODE,
    USER_ID_FOR_SRP,
    SPR_A;

    companion object {
        @JsonCreator
        @JvmStatic
        fun forValue(value: String): ChallengeAttribute =
            values().find { it.name == value } ?: throw InvalidChallengeAttribute(value)
    }
}

class InvalidChallengeAttribute(value: String) : RuntimeException(
    "Invalid challenge parameter attribute received: $value, valid challenge parameter attributes: ${values().map { it.name }}",
)
