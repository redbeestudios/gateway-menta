package com.menta.bff.devices.login.login.revoke.application.service

import arrow.core.Either
import com.menta.bff.devices.login.login.revoke.application.port.out.RevokeTokenPortOut
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class RevokeTokenApplicationService(
    private val revokeTokenOutPort: RevokeTokenPortOut
) {

    fun revoke(revokeToken: RevokeToken): Either<ApplicationError, Unit> =
        revokeTokenOutPort.revoke(revokeToken)
            .logRight { info("user revoke token successful") }

    companion object : CompanionLogger()
}
