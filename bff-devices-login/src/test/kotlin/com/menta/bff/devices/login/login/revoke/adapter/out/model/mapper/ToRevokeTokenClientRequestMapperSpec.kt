package com.menta.bff.devices.login.login.revoke.adapter.out.model.mapper

import com.menta.bff.devices.login.login.revoke.adapter.out.model.RevokeTokenClientRequest
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.domain.UserType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToRevokeTokenClientRequestMapperSpec : FeatureSpec({

    val mapper = ToRevokeTokenClientRequestMapper()

    beforeEach { clearAllMocks() }

    feature("from Revoke Token") {
        scenario("map") {
            val from = RevokeToken(
                token = "TOKEN",
                userType = UserType.MERCHANT
            )

            mapper.mapFrom(from) shouldBe RevokeTokenClientRequest(
                refreshToken = "TOKEN",
                userType = UserType.MERCHANT
            )
        }
    }
})
