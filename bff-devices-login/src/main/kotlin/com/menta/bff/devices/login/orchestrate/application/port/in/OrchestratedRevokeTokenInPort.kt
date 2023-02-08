package com.menta.bff.devices.login.orchestrate.application.port.`in`

import arrow.core.Either
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRevokeToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface OrchestratedRevokeTokenInPort {
    fun revoke(orchestratedUserRevokeToken: OrchestratedUserRevokeToken): Either<ApplicationError, Unit>
}
