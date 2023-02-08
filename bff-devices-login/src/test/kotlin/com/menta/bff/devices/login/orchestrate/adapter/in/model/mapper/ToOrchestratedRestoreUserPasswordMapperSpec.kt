package com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper

import com.menta.bff.devices.login.login.aOrchestratedRestorePasswordRequest
import com.menta.bff.devices.login.login.aOrchestratedRestoreUserPassword
import com.menta.bff.devices.login.shared.domain.mapper.ToOrchestratedRestoreUserPasswordMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToOrchestratedRestoreUserPasswordMapperSpec : FeatureSpec({

    val mapper = ToOrchestratedRestoreUserPasswordMapper()

    feature("map from request") {
        scenario("successful map") {
            val request = aOrchestratedRestorePasswordRequest()
            val result = aOrchestratedRestoreUserPassword()

            mapper.mapFrom(request) shouldBe result
        }
    }
})
