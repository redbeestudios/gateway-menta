package com.menta.api.login.challenge.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.menta.api.login.challenge.domain.ChallengeName.values
import com.menta.api.login.shared.domain.InvalidUserTypeError
import com.menta.api.login.shared.domain.UserType


enum class ChallengeName {
    SMS_MFA,
    SOFTWARE_TOKEN_MFA,
    SELECT_MFA_TYPE,
    MFA_SETUP,
    PASSWORD_VERIFIER,
    CUSTOM_CHALLENGE,
    DEVICE_SRP_AUTH,
    DEVICE_PASSWORD_VERIFIER,
    ADMIN_NO_SRP_AUTH,
    NEW_PASSWORD_REQUIRED;

    companion object {
        @JsonCreator
        @JvmStatic
        fun forValue(value: String): ChallengeName =
            values().find { it.name == value } ?: throw InvalidChallengeNameError(value)
    }

}

class InvalidChallengeNameError(value: String) : RuntimeException(
    "Invalid challenge_name received: $value, valid challenge_name: ${values().map { it.name }}",
)
