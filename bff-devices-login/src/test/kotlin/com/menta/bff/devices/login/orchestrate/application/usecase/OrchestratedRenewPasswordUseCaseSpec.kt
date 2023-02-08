package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.user.application.service.UserApplicationService
import com.menta.bff.devices.login.login.USER
import com.menta.bff.devices.login.login.aOrchestratedRestoreUserPassword
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.timeoutError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrchestratedRenewPasswordUseCaseSpec : FeatureSpec({

    val userApplicationService = mockk<UserApplicationService>()

    val usecase = OrchestratedRenewPasswordUseCase(
        userApplicationService = userApplicationService
    )

    beforeEach { clearAllMocks() }

    feature("restore password") {

        val user = USER
        val type = MERCHANT
        val entity = aOrchestratedRestoreUserPassword()

        scenario("restore successful") {

            every { userApplicationService.renew(user, type) } returns Unit.right()

            usecase.solve(entity) shouldBeRight Unit

            verify(exactly = 1) { userApplicationService.renew(user, type) }
        }

        scenario("restore fail") {

            every { userApplicationService.renew(user, type) } returns timeoutError().left()

            usecase.solve(entity) shouldBeLeft timeoutError()

            verify(exactly = 1) { userApplicationService.renew(user, type) }
        }
    }
})
