package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRevokeTokenRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRevokeToken
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToOrchestratedUserRevokeTokenMapperSpec : FeatureSpec({

    val mapper = ToOrchestratedUserRevokeTokenMapper()

    beforeEach { clearAllMocks() }

    feature("mapper request to entity") {
        scenario("map successfully") {
            val request = OrchestratedRevokeTokenRequest(
                refreshToken = "a refresh token",
                user = "user@user.com",
                userType = MERCHANT,
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserRevokeToken(
                revokeToken = RevokeToken(
                    token = "a refresh token",
                    userType = MERCHANT,
                ),
                user = "user@user.com"
            )
        }
    }
})
