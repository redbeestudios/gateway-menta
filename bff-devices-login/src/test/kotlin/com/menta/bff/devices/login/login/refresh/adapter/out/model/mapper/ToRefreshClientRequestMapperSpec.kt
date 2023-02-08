package com.menta.bff.devices.login.login.refresh.adapter.out.model.mapper

import com.menta.bff.devices.login.login.refresh.adapter.out.model.RefreshClientRequest
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.UserType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToRefreshClientRequestMapperSpec : FeatureSpec({

    val mapper = ToRefreshClientRequestMapper()

    beforeEach { clearAllMocks() }

    feature("from Refresh") {
        scenario("map") {
            val from = Refresh(
                token = "TOKEN",
                userType = UserType.MERCHANT
            )

            mapper.mapFrom(from) shouldBe RefreshClientRequest(
                refreshToken = "TOKEN",
                userType = UserType.MERCHANT
            )
        }
    }
})
