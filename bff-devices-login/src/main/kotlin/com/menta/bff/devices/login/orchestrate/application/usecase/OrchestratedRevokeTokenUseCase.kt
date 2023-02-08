package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.Either
import com.menta.bff.devices.login.login.revoke.application.service.RevokeTokenApplicationService
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRevokeTokenInPort
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRevokeToken
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class OrchestratedRevokeTokenUseCase(
    private val revokeTokenApplicationService: RevokeTokenApplicationService,
) : OrchestratedRevokeTokenInPort {

    override fun revoke(orchestratedUserRevokeToken: OrchestratedUserRevokeToken): Either<ApplicationError, Unit> =
        with(orchestratedUserRevokeToken) {
            revokeToken.doRevoke()
        }

    private fun RevokeToken.doRevoke() =
        revokeTokenApplicationService.revoke(this)
            .logRight { info("user revoke token successful") }

    companion object : CompanionLogger()
}
