package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aNewUser
import com.menta.api.users.aUser
import com.menta.api.users.application.port.out.CreateUserPortOut
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userAlreadyExistsError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateUserUseCaseSpec : FeatureSpec({

    val createUserPortOut = mockk<CreateUserPortOut>()

    val useCase = CreateUserUseCase(
        createUserPortOut = createUserPortOut
    )

    beforeEach { clearAllMocks() }

    feature("create user") {

        scenario("with user valid") {
            val type = MERCHANT
            val newUser = aNewUser()
            val user = aUser()

            every { createUserPortOut.create(newUser) } returns user.right()

            useCase.create(newUser) shouldBeRight user

            verify(exactly = 1) { createUserPortOut.create(newUser) }
        }

        scenario("with user exist") {
            val type = MERCHANT
            val newUser = aNewUser()
            val user = aUser()
            val error = userAlreadyExistsError(user.attributes.email)

            every { createUserPortOut.create(newUser) } returns error.left()

            useCase.create(newUser) shouldBeLeft error

            verify(exactly = 1) { createUserPortOut.create(newUser) }
        }
    }
})
