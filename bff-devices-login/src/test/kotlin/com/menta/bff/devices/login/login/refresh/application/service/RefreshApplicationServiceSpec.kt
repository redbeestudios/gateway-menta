package com.menta.bff.devices.login.login.refresh.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.login.refresh.application.port.out.RefreshPortOut
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class RefreshApplicationServiceSpec : FeatureSpec({

    val refreshPortOut = mockk<RefreshPortOut>()

    val refreshService = RefreshApplicationService(
        refreshOutPort = refreshPortOut
    )

    beforeEach { clearAllMocks() }

    feature("refresh service") {
        val refreshAuth = Refresh(token = "token", userType = MERCHANT)

        scenario("successful") {
            val userAuth = aUserAuthResponseWithToken()

            every { refreshPortOut.refresh(refreshAuth) } returns userAuth.right()

            refreshService.refresh(refreshAuth) shouldBeRight userAuth

            verify(exactly = 1) { refreshPortOut.refresh(refreshAuth) }
        }
        scenario("unsuccessful by unauthorized user") {
            val error = unauthorizedUser()

            every { refreshPortOut.refresh(refreshAuth) } returns error.left()

            refreshService.refresh(refreshAuth) shouldBeLeft error

            verify(exactly = 1) { refreshPortOut.refresh(refreshAuth) }
        }
    }
})
