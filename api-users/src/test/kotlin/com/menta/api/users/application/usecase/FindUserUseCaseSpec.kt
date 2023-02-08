package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aUser
import com.menta.api.users.application.port.out.FindUserPortOut
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userDisabledError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FindUserUseCaseSpec : FeatureSpec({

    val findUserPortOut = mockk<FindUserPortOut>()

    val useCase = FindUserUseCase(
        findUserPortOut = findUserPortOut
    )

    beforeEach { clearAllMocks() }

    feature("find by email and user type") {

        scenario("user found") {

            val type = MERCHANT
            val email = "an email"
            val user = aUser().copy(enabled = true)

            every { findUserPortOut.findBy(email, type) } returns user.right()

            useCase.findBy(email, type) shouldBeRight user

            verify(exactly = 1) { findUserPortOut.findBy(email, type) }
        }

        scenario("error communicating with cognito") {

            val type = MERCHANT
            val email = "an email"
            val error = unauthorizedUser()

            every { findUserPortOut.findBy(email, type) } returns error.left()

            useCase.findBy(email, type) shouldBeLeft error

            verify(exactly = 1) { findUserPortOut.findBy(email, type) }
        }

        scenario("user disabled") {

            val type = MERCHANT
            val email = "an email"
            val user = aUser().copy(enabled = false)
            val error = userDisabledError(user.attributes.email)

            every { findUserPortOut.findBy(email, type) } returns user.right()

            useCase.findBy(email, type) shouldBeLeft error

            verify(exactly = 1) { findUserPortOut.findBy(email, type) }
        }
    }
})
