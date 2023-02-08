package com.menta.bff.devices.login.orchestrate.application.port.`in`

import arrow.core.Either
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserChallenge
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface OrchestratedNewPasswordChallengeInPort {
    fun solve(orchestratedUserChallenge: OrchestratedUserChallenge): Either<ApplicationError, OrchestratedAuth>
}
