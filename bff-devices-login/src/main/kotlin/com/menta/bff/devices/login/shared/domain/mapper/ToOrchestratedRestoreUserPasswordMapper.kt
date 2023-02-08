package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRestorePasswordRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedRestoreUserPassword
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratedRestoreUserPasswordMapper {

    fun mapFrom(request: OrchestratedRestorePasswordRequest): OrchestratedRestoreUserPassword =
        with(request) {
            OrchestratedRestoreUserPassword(
                user = user,
                userType = userType
            )
        }.log { info("orchestrated restore user password mapped: {}", it) }

    companion object : CompanionLogger()
}
