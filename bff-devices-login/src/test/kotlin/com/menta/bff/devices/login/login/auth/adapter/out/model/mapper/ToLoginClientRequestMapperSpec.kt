package com.menta.bff.devices.login.login.auth.adapter.out.model.mapper

import com.menta.bff.devices.login.login.auth.adapter.out.model.LoginClientRequest
import com.menta.bff.devices.login.shared.domain.UserCredentials
import com.menta.bff.devices.login.shared.domain.UserType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToLoginClientRequestMapperSpec : FeatureSpec({

    val mapper = ToLoginClientRequestMapper()

    beforeEach { clearAllMocks() }

    feature("from UserCredentials") {
        scenario("map") {
            val from = UserCredentials(
                user = "user",
                password = "password",
                userType = UserType.MERCHANT
            )

            mapper.mapFrom(from) shouldBe LoginClientRequest(
                user = "user",
                password = "password",
                userType = UserType.MERCHANT
            )
        }
    }
})
