package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRefreshRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRefresh
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratedUserRefreshMapper(
    private val toOrchestratedEntityParametersMapper: ToOrchestratedEntityParametersMapper
) {

    fun mapFrom(request: OrchestratedRefreshRequest): OrchestratedUserRefresh =
        with(request) {
            OrchestratedUserRefresh(
                refresh = Refresh(
                    token = refreshToken,
                    userType = userType,
                ),
                user = user,
                orchestratedEntityParameters = orchestratedEntities?.let {
                    toOrchestratedEntityParametersMapper.mapFrom(it)
                }
            )
        }.log { info("orchestrated user refresh mapped: {}", it) }

    companion object : CompanionLogger()
}
