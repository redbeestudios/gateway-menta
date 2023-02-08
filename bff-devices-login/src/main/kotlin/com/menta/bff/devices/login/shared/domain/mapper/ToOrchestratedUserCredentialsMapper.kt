package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedLoginRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserCredentials
import com.menta.bff.devices.login.shared.domain.UserCredentials
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratedUserCredentialsMapper(
    private val toOrchestratedEntityParametersMapper: ToOrchestratedEntityParametersMapper
) {

    fun mapFrom(request: OrchestratedLoginRequest): OrchestratedUserCredentials =
        with(request) {
            OrchestratedUserCredentials(
                userCredentials = UserCredentials(
                    user = user,
                    password = password,
                    userType = userType
                ),
                orchestratedEntityParameters = orchestratedEntities?.let {
                    toOrchestratedEntityParametersMapper.mapFrom(it)
                }
            )
        }.log { info("orchestrated user credentials mapped: {}", it) }

    companion object : CompanionLogger()
}
