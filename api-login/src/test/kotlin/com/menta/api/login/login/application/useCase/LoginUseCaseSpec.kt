package com.menta.api.login.login.application.useCase

import arrow.core.left
import arrow.core.right
import com.menta.api.login.aUserAuthWithToken
import com.menta.api.login.aUserCredentials
import com.menta.api.login.login.application.port.out.LoginClientOutPort
import com.menta.api.login.login.application.usecase.LoginUseCase
import com.menta.api.login.shared.other.error.model.ApplicationError.Companion.authenticationProviderError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class LoginUseCaseSpec : FeatureSpec({

    feature("login") {

        val loginClientOutPort = mockk<LoginClientOutPort>()

        val useCase = LoginUseCase(
            loginClientOutPort = loginClientOutPort
        )

        beforeEach { clearAllMocks() }

        scenario("successful login") {
            val userCredentials = aUserCredentials()
            val userToken = aUserAuthWithToken()

            // given mocked dependencies
            every { loginClientOutPort.login(userCredentials) } returns userToken.right()

            // expect that
            useCase.login(userCredentials) shouldBeRight userToken

            // dependencies called
            verify(exactly = 1) { loginClientOutPort.login(userCredentials) }
        }

        scenario("login error") {
            val userCredentials = aUserCredentials()
            val ex = mockk<Throwable>()

            // given mocked dependencies
            every { loginClientOutPort.login(userCredentials) } returns authenticationProviderError(ex).left()

            // expect that
            useCase.login(userCredentials) shouldBeLeft authenticationProviderError(ex)

            // dependencies called
            verify(exactly = 1) { loginClientOutPort.login(userCredentials) }
        }
    }
})
