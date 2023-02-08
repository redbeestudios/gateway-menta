package com.menta.bff.devices.login.orchestrate.domain.mapper

import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratedAuthMapper {
    fun mapFrom(userAuth: UserAuth, orchestratedEntities: OrchestratedEntities?): OrchestratedAuth =
        OrchestratedAuth(
            userAuth = userAuth,
            orchestratedEntities = orchestratedEntities
        )
            .log { info("orchestrated auth mapped: {}", it) }

    companion object : CompanionLogger()
}
