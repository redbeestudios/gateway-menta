package com.menta.api.users.authorities.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.authorities.anUserAssignAuthority
import com.menta.api.users.authorities.application.port.out.AssignAuthorityPortOut
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.timeoutError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AssignAuthorityUseCaseSpec : FeatureSpec({

    val portOut = mockk<AssignAuthorityPortOut>()
    val useCase = AssignAuthorityUseCase(
        assignAuthorityPortOut = portOut
    )

    beforeEach { clearAllMocks() }

    feature("assign authority to user") {
        val userAssignAuthority = anUserAssignAuthority
        val error = timeoutError()

        scenario("authority assigned") {
            every { portOut.assign(userAssignAuthority) } returns Unit.right()

            useCase.assign(userAssignAuthority) shouldBeRight Unit

            verify(exactly = 1) { portOut.assign(userAssignAuthority) }
        }
        scenario("out port fails") {
            every { portOut.assign(userAssignAuthority) } returns error.left()

            useCase.assign(userAssignAuthority) shouldBeLeft error

            verify(exactly = 1) { portOut.assign(userAssignAuthority) }
        }
    }
})
