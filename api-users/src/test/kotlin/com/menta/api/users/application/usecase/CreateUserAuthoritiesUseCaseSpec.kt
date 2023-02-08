package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aUser
import com.menta.api.users.application.port.out.CreatedUserEventPortOut
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.queueProducerNotWritten
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateUserAuthoritiesUseCaseSpec : FeatureSpec({

    val createUserPortOut = mockk<CreatedUserEventPortOut>()

    val useCase = CreateUserAuthoritiesUseCase(
        createUserPortOut = createUserPortOut
    )

    beforeEach { clearAllMocks() }

    feature("create user authorities") {

        scenario("create successfully") {
            val user = aUser()

            every { createUserPortOut.produce(user) } returns Unit.right()

            useCase.execute(user) shouldBeRight Unit

            verify(exactly = 1) { createUserPortOut.produce(user) }
        }

        scenario("whit queue producer not written error") {
            val user = aUser()
            val error = queueProducerNotWritten("topic")

            every { createUserPortOut.produce(user) } returns error.left()

            createUserPortOut.produce(user) shouldBeLeft error

            verify(exactly = 1) { createUserPortOut.produce(user) }
        }
    }
})
