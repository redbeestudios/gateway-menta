package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.shared.adapter.out.model.OrchestratedEntitiesRequest
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratedEntityParametersMapper {
    fun mapFrom(orchestratedEntitiesRequest: OrchestratedEntitiesRequest) =
        with(orchestratedEntitiesRequest) {
            OrchestratedEntityParameters(
                terminalSerialCode = terminalSerialCode,
                terminalModel = terminalModel
            )
                .log { info("orchestrated entities parameters mapped: {}", it) }
        }

    companion object : CompanionLogger()
}
