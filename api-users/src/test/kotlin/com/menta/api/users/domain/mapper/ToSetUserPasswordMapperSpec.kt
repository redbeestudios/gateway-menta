package com.menta.api.users.domain.mapper

import com.menta.api.users.adapter.`in`.model.SetUserPasswordRequest
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.domain.UserType
import com.menta.api.users.email
import com.menta.api.users.password
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToSetUserPasswordMapperSpec : FeatureSpec({

    val mapper = ToSetUserPasswordMapper()

    feature("map set user password from set user password request") {

        scenario("successful map") {
            mapper.mapFrom(
                SetUserPasswordRequest(
                    userType = UserType.MERCHANT,
                    email = email,
                    password = "$password",
                    permanent = false
                )
            ) shouldBe SetUserPassword(
                type = UserType.MERCHANT,
                email = email,
                password = "$password",
                permanent = false
            )
        }
    }
})
