package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest
import com.menta.api.users.email
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToAdminGetUserRequestMapperSpec : FeatureSpec({

    val mapper = ToAdminGetUserRequestMapper()

    feature("map cognito request from email and pool") {
        scenario("map") {
            mapper.mapFrom(
                email = email,
                UserPool(
                    code = "a pool",
                    clientId = "a client id"
                )
            ) shouldBe AdminGetUserRequest()
                .withUserPoolId("a pool")
                .withUsername(email)
        }
    }
})
