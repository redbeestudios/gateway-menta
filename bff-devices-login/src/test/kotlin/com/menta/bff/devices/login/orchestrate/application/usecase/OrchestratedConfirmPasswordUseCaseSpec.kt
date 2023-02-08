package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.user.application.service.UserApplicationService
import com.menta.bff.devices.login.login.aConfirmRestoreUserPassword
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.timeoutError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrchestratedConfirmPasswordUseCaseSpec : FeatureSpec({

    val userApplicationService = mockk<UserApplicationService>()

    val usecase = OrchestratedConfirmPasswordUseCase(
        userApplicationService = userApplicationService
    )

    beforeEach { clearAllMocks() }

    feature("confirm new password") {
        val entity = aConfirmRestoreUserPassword()

        scenario("confirm successful") {

            every { userApplicationService.confirmPassword(entity) } returns Unit.right()

            usecase.confirm(entity) shouldBeRight Unit

            verify(exactly = 1) { userApplicationService.confirmPassword(entity) }
        }

        scenario("confirm fail") {

            every { userApplicationService.confirmPassword(entity) } returns timeoutError().left()

            usecase.confirm(entity) shouldBeLeft timeoutError()

            verify(exactly = 1) { userApplicationService.confirmPassword(entity) }
        }
    }
})
