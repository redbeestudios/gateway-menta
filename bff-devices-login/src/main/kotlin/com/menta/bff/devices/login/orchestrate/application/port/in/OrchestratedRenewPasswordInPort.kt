package com.menta.bff.devices.login.orchestrate.application.port.`in`

import arrow.core.Either
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedRestoreUserPassword
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface OrchestratedRenewPasswordInPort {
    fun solve(orchestratedRestoreUserPassword: OrchestratedRestoreUserPassword): Either<ApplicationError, Unit>
}
