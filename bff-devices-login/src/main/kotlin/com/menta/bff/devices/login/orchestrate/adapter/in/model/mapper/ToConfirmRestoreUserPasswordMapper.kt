package com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.ConfirmRestoreUserPasswordRequest
import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.mapper.ToOrchestratorResponseMapper.Companion.log
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
import org.springframework.stereotype.Component

@Component
class ToConfirmRestoreUserPasswordMapper {

    fun mapFrom(request: ConfirmRestoreUserPasswordRequest) =
        with(request) {
            ConfirmRestoreUserPassword(
                user = user,
                userType = userType,
                newPassword = newPassword,
                code = code
            )
        }.log { info("ConfirmRestoreUserPassword mapped: {}", it) }
}
