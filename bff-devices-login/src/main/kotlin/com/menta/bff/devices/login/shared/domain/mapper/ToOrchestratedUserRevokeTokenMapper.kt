package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRevokeTokenRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRevokeToken
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratedUserRevokeTokenMapper {

    fun mapFrom(request: OrchestratedRevokeTokenRequest): OrchestratedUserRevokeToken =
        with(request) {
            OrchestratedUserRevokeToken(
                revokeToken = RevokeToken(
                    token = refreshToken,
                    userType = userType,
                ),
                user = user
            )
        }.log { info("orchestrated user revoke token mapped: {}", it) }

    companion object : CompanionLogger()
}
