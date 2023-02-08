package com.menta.bff.devices.login.shared.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.menta.bff.devices.login.shared.domain.ChallengeName.values

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
