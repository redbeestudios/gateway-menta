package com.menta.bff.devices.login.login.refresh.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface RefreshPortOut {
    fun refresh(refresh: Refresh): Either<ApplicationError, UserAuth>
}
