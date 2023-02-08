package com.menta.bff.devices.login.orchestrate.application.port.`in`

import arrow.core.Either
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserCredentials
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface OrchestratedLoginInPort {
    fun login(orchestratedUserCredentials: OrchestratedUserCredentials): Either<ApplicationError, OrchestratedAuth>
}
