package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.right
import com.menta.bff.devices.login.login.revoke.application.service.RevokeTokenApplicationService
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRevokeToken
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrchestratedRevokeTokenUseCaseSpec : FeatureSpec({

    val revokeTokenApplicationService = mockk<RevokeTokenApplicationService>()

    val client = OrchestratedRevokeTokenUseCase(
        revokeTokenApplicationService = revokeTokenApplicationService,
    )

    beforeEach { clearAllMocks() }

    feature("orchestrated revoke token") {
        val revokeCredentials = RevokeToken(token = "token", userType = MERCHANT)

        scenario("revoke token successful without orchestrated entities") {
            val orchestratedUserCredentials = OrchestratedUserRevokeToken(
                user = "user",
                revokeToken = revokeCredentials
            )

            every { revokeTokenApplicationService.revoke(revokeCredentials) } returns Unit.right()

            client.revoke(orchestratedUserCredentials) shouldBeRight Unit

            verify(exactly = 1) { revokeTokenApplicationService.revoke(revokeCredentials) }
        }
    }
})
