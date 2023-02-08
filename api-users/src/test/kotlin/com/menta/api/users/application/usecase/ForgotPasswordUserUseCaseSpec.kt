package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.application.port.out.ForgotPasswordUserPortOut
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ForgotPasswordUserUseCaseSpec : FeatureSpec({

    val forgotPasswordUserPortOut = mockk<ForgotPasswordUserPortOut>()

    val useCase = ForgotPasswordUserUseCase(
        forgotPasswordUser = forgotPasswordUserPortOut
    )

    beforeEach { clearAllMocks() }

    feature("forgot password by email and user type") {
        scenario("user exist") {
            val type = MERCHANT
            val email = "an email"

            every { forgotPasswordUserPortOut.retrieve(email, type) } returns Unit.right()

            useCase.retrieve(email, type) shouldBeRight Unit

            verify(exactly = 1) { forgotPasswordUserPortOut.retrieve(email, type) }
        }
        scenario("error communicating with cognito") {
            val type = MERCHANT
            val email = "an email"
            val error = unauthorizedUser()

            every { forgotPasswordUserPortOut.retrieve(email, type) } returns error.left()

            useCase.retrieve(email, type) shouldBeLeft error

            verify(exactly = 1) { forgotPasswordUserPortOut.retrieve(email, type) }
        }
    }
})
