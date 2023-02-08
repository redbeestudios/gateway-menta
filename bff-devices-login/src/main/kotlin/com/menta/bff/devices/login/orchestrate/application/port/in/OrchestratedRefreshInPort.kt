package com.menta.bff.devices.login.orchestrate.application.port.`in`

import arrow.core.Either
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRefresh
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface OrchestratedRefreshInPort {
    fun refresh(orchestratedUserRefresh: OrchestratedUserRefresh): Either<ApplicationError, OrchestratedAuth>
}
