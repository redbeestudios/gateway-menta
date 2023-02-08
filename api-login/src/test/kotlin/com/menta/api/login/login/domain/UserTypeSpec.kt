package com.menta.api.login.login.domain

import com.menta.api.login.shared.domain.InvalidUserTypeError
import com.menta.api.login.shared.domain.UserType
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec

class UserTypeSpec : FeatureSpec({
    feature("UserType") {
        scenario("with unknown type") {
            shouldThrowExactly<InvalidUserTypeError> {
                UserType.forValue("unknownType")
            }
        }
    }
})
