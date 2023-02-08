package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.Either
import com.menta.bff.devices.login.entities.user.application.service.UserApplicationService
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRenewPasswordInPort
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedRestoreUserPassword
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class OrchestratedRenewPasswordUseCase(
    private val userApplicationService: UserApplicationService
) : OrchestratedRenewPasswordInPort {

    override fun solve(orchestratedRestoreUserPassword: OrchestratedRestoreUserPassword): Either<ApplicationError, Unit> =
        orchestratedRestoreUserPassword.doSolve()

    private fun OrchestratedRestoreUserPassword.doSolve() =
        userApplicationService.renew(this.user, this.userType)
            .logRight { info("user password restore successfully") }

    companion object : CompanionLogger()
}
