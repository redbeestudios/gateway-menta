package com.menta.bff.devices.login.login.revoke.application.service

import arrow.core.right
import com.menta.bff.devices.login.login.revoke.application.port.out.RevokeTokenPortOut
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class RevokeTokenApplicationServiceSpec : FeatureSpec({

    val revokeTokenPortOut = mockk<RevokeTokenPortOut>()

    val revokeTokenService = RevokeTokenApplicationService(
        revokeTokenOutPort = revokeTokenPortOut
    )

    beforeEach { clearAllMocks() }

    feature("revoke token service") {
        val revokeAuth = RevokeToken(token = "token", userType = MERCHANT)

        scenario("successful") {
            every { revokeTokenPortOut.revoke(revokeAuth) } returns Unit.right()

            revokeTokenService.revoke(revokeAuth) shouldBeRight Unit

            verify(exactly = 1) { revokeTokenPortOut.revoke(revokeAuth) }
        }
    }
})
