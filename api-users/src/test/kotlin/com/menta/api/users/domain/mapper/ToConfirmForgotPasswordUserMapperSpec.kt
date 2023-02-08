package com.menta.api.users.domain.mapper

import com.menta.api.users.adapter.`in`.model.ConfirmForgotPasswordUserRequest
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.domain.UserType
import com.menta.api.users.email
import com.menta.api.users.password
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToConfirmForgotPasswordUserMapperSpec : FeatureSpec({

    val mapper = ToConfirmForgotPasswordUserMapper()

    feature("map confirm forgot user password from confirm password user request") {

        scenario("successful map") {
            mapper.mapFrom(
                ConfirmForgotPasswordUserRequest(
                    code = "code",
                    password = "$password",
                ),
                email = email,
                type = UserType.MERCHANT
            ) shouldBe ConfirmForgotPasswordUser(
                type = UserType.MERCHANT,
                email = email,
                password = "$password",
                code = "code"
            )
        }
    }
})
