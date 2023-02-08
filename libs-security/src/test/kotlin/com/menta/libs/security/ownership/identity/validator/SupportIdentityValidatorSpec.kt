package com.menta.libs.security.ownership.identity.validator

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class SupportIdentityValidatorSpec: FeatureSpec({

    feature("validate") {
        scenario("validated") {
            DefaultSupportIdentityValidator().validate(mockk(), mockk(), mockk()) shouldBe Unit
        }
    }
}) {
}