package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.application.port.out.DeleteUserPortOut
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DeleteUserUseCaseSpec : FeatureSpec({

    val deleteUserPortOut = mockk<DeleteUserPortOut>()

    val useCase = DeleteUserUseCase(
        deleteUserPortOut = deleteUserPortOut
    )

    beforeEach { clearAllMocks() }

    feature("delete by email and user type") {

        scenario("user exist") {

            val type = MERCHANT
            val email = "an email"

            every { deleteUserPortOut.deleteBy(email, type) } returns Unit.right()

            useCase.deleteBy(email, type) shouldBeRight Unit

            verify(exactly = 1) { deleteUserPortOut.deleteBy(email, type) }
        }

        scenario("error communicating with cognito") {

            val type = MERCHANT
            val email = "an email"
            val error = unauthorizedUser()

            every { deleteUserPortOut.deleteBy(email, type) } returns error.left()

            useCase.deleteBy(email, type) shouldBeLeft error

            verify(exactly = 1) { deleteUserPortOut.deleteBy(email, type) }
        }
    }
})
