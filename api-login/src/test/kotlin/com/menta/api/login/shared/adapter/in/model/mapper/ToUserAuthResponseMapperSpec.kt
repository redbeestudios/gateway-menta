package com.menta.api.login.shared.adapter.`in`.model.mapper

import com.menta.api.login.aUserAuthResponseWithChallenge
import com.menta.api.login.aUserAuthResponseWithToken
import com.menta.api.login.aUserAuthWithChallenge
import com.menta.api.login.aUserAuthWithToken
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToUserAuthResponseMapperSpec : FeatureSpec({

    val mapper = ToUserAuthResponseMapper()

    feature("map user auth response from user auth") {
        scenario("map with token") {
            mapper.mapFrom(
                aUserAuthWithToken()
            ) shouldBe aUserAuthResponseWithToken()
        }

        scenario("map with challenge") {
            mapper.mapFrom(
                aUserAuthWithChallenge()
            ) shouldBe aUserAuthResponseWithChallenge()
        }

    }
})
