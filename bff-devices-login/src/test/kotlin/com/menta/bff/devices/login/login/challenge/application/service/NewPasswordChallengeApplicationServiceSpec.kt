package com.menta.bff.devices.login.login.challenge.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.login.challenge.application.port.out.NewPasswordChallengePortOut
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.UserType.CUSTOMER
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class NewPasswordChallengeApplicationServiceSpec : FeatureSpec({

    val newPasswordChallengePortOut = mockk<NewPasswordChallengePortOut>()

    val refreshService = NewPasswordChallengeApplicationService(
        newPasswordChallengePortOut = newPasswordChallengePortOut
    )

    beforeEach { clearAllMocks() }

    feature("new password challenge service") {
        val newPasswordChallenge = NewPasswordChallengeSolution(
            session = "a session",
            user = "user@user.com",
            userType = CUSTOMER,
            newPassword = "a new password"
        )

        scenario("successful") {
            val userAuth = aUserAuthResponseWithToken()

            every { newPasswordChallengePortOut.solve(newPasswordChallenge) } returns userAuth.right()

            refreshService.solve(newPasswordChallenge) shouldBeRight userAuth

            verify(exactly = 1) { newPasswordChallengePortOut.solve(newPasswordChallenge) }
        }
        scenario("unsuccessful by unauthorized user") {
            val error = unauthorizedUser()

            every { newPasswordChallengePortOut.solve(newPasswordChallenge) } returns error.left()

            refreshService.solve(newPasswordChallenge) shouldBeLeft error

            verify(exactly = 1) { newPasswordChallengePortOut.solve(newPasswordChallenge) }
        }
    }
})
