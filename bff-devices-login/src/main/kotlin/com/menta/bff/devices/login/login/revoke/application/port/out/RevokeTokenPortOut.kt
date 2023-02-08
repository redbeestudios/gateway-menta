package com.menta.bff.devices.login.login.revoke.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.shared.domain.RevokeToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface RevokeTokenPortOut {
    fun revoke(revokeToken: RevokeToken): Either<ApplicationError, Unit>
}
