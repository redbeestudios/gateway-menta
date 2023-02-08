package com.menta.bff.devices.login.entities.user.adapter.out.mapper

import com.menta.bff.devices.login.entities.user.adapter.out.models.ConfirmPasswordUserRequest
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserRequestMapper {

    fun mapFrom(confirmRestoreUserPassword: ConfirmRestoreUserPassword) =
        confirmRestoreUserPassword.let {
            ConfirmPasswordUserRequest(
                code = it.code,
                password = it.newPassword
            )
        }.log { info("confirmPasswordUserRequest mapped successfully") }

    companion object : CompanionLogger()
}
