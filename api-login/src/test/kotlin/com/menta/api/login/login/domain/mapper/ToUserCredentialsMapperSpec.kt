package com.menta.api.login.login.domain.mapper

import com.menta.api.login.aLoginRequest
import com.menta.api.login.aUserCredentials
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToUserCredentialsMapperSpec : FeatureSpec({

    val mapper = ToUserCredentialsMapper()

    feature("map loginRequest to userCredentials") {
        scenario("successful map") {
            mapper.mapFrom(
                aLoginRequest()
            ) shouldBe aUserCredentials()
        }
    }
})
