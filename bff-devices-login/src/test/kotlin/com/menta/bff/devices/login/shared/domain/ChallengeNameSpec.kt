package com.menta.bff.devices.login.shared.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ChallengeNameSpec : FeatureSpec({

    feature("map from name to ChallengeName") {
        scenario("with other type value") {
            val exception = shouldThrow<InvalidChallengeNameError> {
                ChallengeName.forValue("OTHER")
            }
            exception.message shouldBe "Invalid challenge_name received: OTHER, valid challenge_name: [SMS_MFA, SOFTWARE_TOKEN_MFA, SELECT_MFA_TYPE, MFA_SETUP, PASSWORD_VERIFIER, CUSTOM_CHALLENGE, DEVICE_SRP_AUTH, DEVICE_PASSWORD_VERIFIER, ADMIN_NO_SRP_AUTH, NEW_PASSWORD_REQUIRED]"
        }
        scenario("successful mapping SMS_MFA") {
            ChallengeName.forValue("SMS_MFA") shouldBe ChallengeName.SMS_MFA
        }
    }
})
