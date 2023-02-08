package com.menta.bff.devices.login.login.auth.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.login.aUserCredentials
import com.menta.bff.devices.login.login.auth.application.port.out.LoginPortOut
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class LoginApplicationServiceSpec : FeatureSpec({

    val loginPortOut = mockk<LoginPortOut>()

    val loginService = LoginApplicationService(
        loginPortOut = loginPortOut
    )

    beforeEach { clearAllMocks() }

    feature("login service") {
        scenario("successful") {
            val userCredentials = aUserCredentials()
            val userAuth = aUserAuthResponseWithToken()

            every { loginPortOut.login(userCredentials) } returns userAuth.right()

            loginService.login(userCredentials) shouldBeRight userAuth

            verify(exactly = 1) { loginPortOut.login(userCredentials) }
        }
        scenario("unsuccessful by user not found") {
            val userCredentials = aUserCredentials()
            val error = notFound("user ${userCredentials.user} for ${userCredentials.userType} not found")

            every { loginPortOut.login(userCredentials) } returns error.left()

            loginService.login(userCredentials) shouldBeLeft error

            verify(exactly = 1) { loginPortOut.login(userCredentials) }
        }
    }
})
