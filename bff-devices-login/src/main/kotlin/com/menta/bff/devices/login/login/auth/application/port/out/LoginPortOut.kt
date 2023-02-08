package com.menta.bff.devices.login.login.auth.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserCredentials
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface LoginPortOut {
    fun login(credentials: UserCredentials): Either<ApplicationError, UserAuth>
}
