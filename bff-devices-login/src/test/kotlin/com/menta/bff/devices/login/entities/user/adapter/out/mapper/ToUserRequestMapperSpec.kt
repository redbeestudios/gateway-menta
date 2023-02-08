package com.menta.bff.devices.login.entities.user.adapter.out.mapper

import com.menta.bff.devices.login.login.aConfirmPasswordUserRequest
import com.menta.bff.devices.login.login.aConfirmRestoreUserPassword
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToUserRequestMapperSpec : FeatureSpec({

    val mapper = ToUserRequestMapper()

    feature("map from request") {
        scenario("successful map") {
            val entity = aConfirmRestoreUserPassword()
            val result = aConfirmPasswordUserRequest()

            mapper.mapFrom(entity) shouldBe result
        }
    }
})
