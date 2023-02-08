package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aSetUserPassword
import com.menta.api.users.application.port.out.SetUserPasswordPortOut
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SetUserPasswordUseCaseSpec : FeatureSpec({

    val setUserPasswordPortOut = mockk<SetUserPasswordPortOut>()

    val useCase = SetUserPasswordUseCase(
        setUserPasswordPortOut = setUserPasswordPortOut
    )

    beforeEach { clearAllMocks() }

    feature("set user password") {
        val setUserPassword = aSetUserPassword()

        scenario("user exist") {

            every { setUserPasswordPortOut.setPassword(setUserPassword) } returns Unit.right()

            useCase.setPassword(setUserPassword) shouldBeRight Unit

            verify(exactly = 1) { setUserPasswordPortOut.setPassword(setUserPassword) }
        }
        scenario("error communicating with cognito") {
            val error = unauthorizedUser()

            every { setUserPasswordPortOut.setPassword(setUserPassword) } returns error.left()

            useCase.setPassword(setUserPassword) shouldBeLeft error

            verify(exactly = 1) { setUserPasswordPortOut.setPassword(setUserPassword) }
        }
    }
})
