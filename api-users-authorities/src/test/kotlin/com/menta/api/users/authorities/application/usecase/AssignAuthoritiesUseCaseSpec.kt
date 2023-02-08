package com.menta.api.users.authorities.application.usecase

import arrow.core.right
import com.menta.api.users.authorities.anUserAssignAuthority
import com.menta.api.users.authorities.application.port.out.AssignUserAuthorityPortOut
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.timeoutError
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AssignAuthoritiesUseCaseSpec : FeatureSpec({

    val portOut = mockk<AssignUserAuthorityPortOut>()
    val useCase = AssignAuthoritiesUseCase(
        assignUserAuthorityPortOut = portOut
    )

    beforeEach { clearAllMocks() }

    feature("assign authorities to user") {
        val userAssignAuthority = anUserAssignAuthority
        val error = timeoutError()

        scenario("authorities assigned") {
            every { portOut.produce(userAssignAuthority) } returns Unit.right()

            useCase.execute(listOf(userAssignAuthority)) shouldBeRight Unit

            verify(exactly = 1) { portOut.produce(userAssignAuthority) }
        }
    }
})
