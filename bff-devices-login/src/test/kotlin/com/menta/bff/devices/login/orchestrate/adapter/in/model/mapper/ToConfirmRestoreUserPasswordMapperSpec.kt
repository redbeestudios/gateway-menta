package com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper

import com.menta.bff.devices.login.login.aConfirmRestoreUserPassword
import com.menta.bff.devices.login.login.aConfirmRestoreUserPasswordRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToConfirmRestoreUserPasswordMapperSpec : FeatureSpec({

    val mapper = ToConfirmRestoreUserPasswordMapper()

    feature("map from request") {
        scenario("successful map") {
            val request = aConfirmRestoreUserPasswordRequest()
            val result = aConfirmRestoreUserPassword()

            mapper.mapFrom(request) shouldBe result
        }
    }
})
