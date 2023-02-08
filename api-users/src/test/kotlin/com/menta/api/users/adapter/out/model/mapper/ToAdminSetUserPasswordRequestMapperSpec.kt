package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.email
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToAdminSetUserPasswordRequestMapperSpec : FeatureSpec({

    val mapper = ToAdminSetUserPasswordRequestMapper()

    feature("map cognito request from set user password and pool") {
        scenario("map") {
            mapper.mapFrom(
                setUserPassword = SetUserPassword(
                    type = MERCHANT,
                    email = email,
                    password = "password",
                    permanent = true
                ),
                UserPool(
                    code = "a pool",
                    clientId = "a client id"
                )
            ) shouldBe AdminSetUserPasswordRequest()
                .withUsername(email)
                .withUserPoolId("a pool")
                .withPassword("password")
                .withPermanent(true)
        }
    }
})
