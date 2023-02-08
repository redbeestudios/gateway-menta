package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aConfirmForgotPasswordUser
import com.menta.api.users.application.port.out.ConfirmPasswordForgotUserPortOut
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ConfirmForgotPasswordUserUseCaseSpec : FeatureSpec({

    val confirmPasswordForgotUserPortOut = mockk<ConfirmPasswordForgotUserPortOut>()

    val useCase = ConfirmForgotPasswordUserUseCase(
        confirmForgotPasswordUser = confirmPasswordForgotUserPortOut
    )

    beforeEach { clearAllMocks() }

    feature("confirm forgot password") {
        scenario("successful change") {
            val confirmForgotUser = aConfirmForgotPasswordUser()

            every { confirmPasswordForgotUserPortOut.confirm(confirmForgotUser) } returns Unit.right()

            useCase.confirm(confirmForgotUser) shouldBeRight Unit

            verify(exactly = 1) { confirmPasswordForgotUserPortOut.confirm(confirmForgotUser) }
        }
        scenario("error communicating with cognito") {
            val confirmForgotUser = aConfirmForgotPasswordUser()
            val error = unauthorizedUser()

            every { confirmPasswordForgotUserPortOut.confirm(confirmForgotUser) } returns error.left()

            useCase.confirm(confirmForgotUser) shouldBeLeft error

            verify(exactly = 1) { confirmPasswordForgotUserPortOut.confirm(confirmForgotUser) }
        }
    }
})
