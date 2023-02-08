package com.menta.bff.devices.login.shared.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ChallengeAttributeSpec : FeatureSpec({

    feature("map from name to ChallengeAttribute") {
        scenario("with other type value") {
            val exception = shouldThrow<InvalidChallengeAttribute> {
                ChallengeAttribute.forValue("OTHER")
            }
            exception.message shouldBe "Invalid challenge parameter attribute received: OTHER, valid challenge parameter attributes: [USERNAME, SMS_MFA_CODE, PASSWORD_CLAIM_SIGNATURE, PASSWORD_CLAIM_SECRET_BLOCK, TIMESTAMP, DEVICE_KEY, NEW_PASSWORD, SECRET_HASH, SOFTWARE_TOKEN_MFA_CODE, USER_ID_FOR_SRP, SPR_A]"
        }
        scenario("successful mapping SMS_MFA") {
            ChallengeAttribute.forValue("USERNAME") shouldBe ChallengeAttribute.USERNAME
        }
    }
})
