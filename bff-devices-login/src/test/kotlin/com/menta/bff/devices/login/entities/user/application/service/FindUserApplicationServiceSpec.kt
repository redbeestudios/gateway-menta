package com.menta.bff.devices.login.entities.user.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.user.aMerchantUser
import com.menta.bff.devices.login.entities.user.application.port.out.ConfirmUserPasswordPortOut
import com.menta.bff.devices.login.entities.user.application.port.out.FindUserPortOut
import com.menta.bff.devices.login.entities.user.application.port.out.RestoreUserPasswordPortOut
import com.menta.bff.devices.login.login.aConfirmRestoreUserPassword
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.timeoutError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.reactive.function.client.WebClientResponseException

class FindUserApplicationServiceSpec : FeatureSpec({
    val findUserPortOut = mockk<FindUserPortOut>()
    val restoreUserPasswordPortOut = mockk<RestoreUserPasswordPortOut>()
    val confirmUserPasswordPortOut = mockk<ConfirmUserPasswordPortOut>()
    val service = UserApplicationService(findUserPortOut, restoreUserPasswordPortOut, confirmUserPasswordPortOut)

    beforeEach { clearAllMocks() }

    feature("find by email and type") {
        val email = "email@menta.global"
        val type = MERCHANT

        scenario("user found") {
            val user = aMerchantUser()

            every { findUserPortOut.findBy(email, type) } returns user.right()

            service.find(email, type) shouldBeRight user

            verify(exactly = 1) { findUserPortOut.findBy(email, type) }
        }

        scenario("error searching for user") {
            val error = ApplicationError.clientError(WebClientResponseException(500, "a status text", null, null, null))

            every { findUserPortOut.findBy(email, type) } returns error.left()

            service.find(email, type) shouldBeLeft error

            verify(exactly = 1) { findUserPortOut.findBy(email, type) }
        }
    }

    feature("restore password") {
        val email = "email@menta.global"
        val type = MERCHANT

        scenario("restore successfully") {

            every { restoreUserPasswordPortOut.resolve(email, type) } returns Unit.right()

            service.renew(email, type) shouldBeRight Unit

            verify(exactly = 1) { restoreUserPasswordPortOut.resolve(email, type) }
        }

        scenario("restore with timeout error") {

            every { restoreUserPasswordPortOut.resolve(email, type) } returns timeoutError().left()

            service.renew(email, type) shouldBeLeft timeoutError()

            verify(exactly = 1) { restoreUserPasswordPortOut.resolve(email, type) }
        }
    }

    feature("confirm password") {
        val entity = aConfirmRestoreUserPassword()

        scenario("confirm successfully") {

            every { confirmUserPasswordPortOut.confirm(entity) } returns Unit.right()

            service.confirmPassword(entity) shouldBeRight Unit

            verify(exactly = 1) { confirmUserPasswordPortOut.confirm(entity) }
        }

        scenario("confirm with timeout error") {

            every { confirmUserPasswordPortOut.confirm(entity) } returns timeoutError().left()

            service.confirmPassword(entity) shouldBeLeft timeoutError()

            verify(exactly = 1) { confirmUserPasswordPortOut.confirm(entity) }
        }
    }
})
