package com.menta.api.users.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class UserTypeSpec : FeatureSpec({

    feature("map from name to UserType") {
        scenario("with other type value") {
            val exception = shouldThrow<InvalidUserTypeError> {
                UserType.forValue("OTHER")
            }
            exception.message shouldBe "Invalid user_type received: OTHER, valid user_type: [MERCHANT, CUSTOMER, SUPPORT]"
        }
        scenario("successful mapping MERCHANT") {
            UserType.forValue("MERCHANT") shouldBe UserType.MERCHANT
        }
        scenario("successful mapping CUSTOMER") {
            UserType.forValue("CUSTOMER") shouldBe UserType.CUSTOMER
        }
        scenario("successful mapping SUPPORT") {
            UserType.forValue("SUPPORT") shouldBe UserType.SUPPORT
        }
    }
})
