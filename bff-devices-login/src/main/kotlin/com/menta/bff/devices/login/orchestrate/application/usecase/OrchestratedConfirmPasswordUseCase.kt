package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.Either
import com.menta.bff.devices.login.entities.user.application.service.UserApplicationService
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedConfirmPasswordInPort
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class OrchestratedConfirmPasswordUseCase(
    private val userApplicationService: UserApplicationService
) : OrchestratedConfirmPasswordInPort {

    override fun confirm(confirmRestoreUserPassword: ConfirmRestoreUserPassword): Either<ApplicationError, Unit> =
        confirmRestoreUserPassword.doConfirm()

    private fun ConfirmRestoreUserPassword.doConfirm() =
        userApplicationService.confirmPassword(this)
            .logRight { info("user password confirm successfully") }

    companion object : CompanionLogger()
}
