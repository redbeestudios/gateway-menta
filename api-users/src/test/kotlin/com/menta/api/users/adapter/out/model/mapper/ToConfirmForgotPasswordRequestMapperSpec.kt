package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.email
import com.menta.api.users.password
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToConfirmForgotPasswordRequestMapperSpec : FeatureSpec({

    val mapper = ToConfirmForgotPasswordRequestMapper()

    feature("map cognito request from confirm forgot password user") {
        scenario("map") {
            mapper.mapFrom(
                confirmForgotPasswordUser = ConfirmForgotPasswordUser(
                    type = MERCHANT,
                    email = email,
                    code = "code",
                    password = "new password"
                ),
                UserPool(
                    code = "a pool",
                    clientId = "a client id"
                )
            ) shouldBe ConfirmForgotPasswordRequest()
                .withUsername(email)
                .withClientId("a client id")
                .withConfirmationCode("code")
                .withPassword("new password")
        }
    }
})
