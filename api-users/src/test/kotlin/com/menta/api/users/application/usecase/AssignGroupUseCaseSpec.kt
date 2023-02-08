package com.menta.api.users.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.users.aGroupAssignation
import com.menta.api.users.application.port.out.AssignGroupPortOut
import com.menta.api.users.shared.other.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AssignGroupUseCaseSpec : FeatureSpec({

    val portOut = mockk<AssignGroupPortOut>()
    val useCase = AssignGroupUseCase(assignGroupPortOut = portOut)

    beforeEach { clearAllMocks() }

    feature("assign group to user") {

        scenario("group assigned") {
            val group = aGroupAssignation()

            every { portOut.assign(group) } returns group.right()

            useCase.assign(group) shouldBeRight group

            verify(exactly = 1) { portOut.assign(group) }
        }

        scenario("out port fails") {
            val group = aGroupAssignation()
            val error = ApplicationError.unauthorizedUser()

            every { portOut.assign(group) } returns error.left()

            useCase.assign(group) shouldBeLeft error

            verify(exactly = 1) { portOut.assign(group) }
        }
    }
})
