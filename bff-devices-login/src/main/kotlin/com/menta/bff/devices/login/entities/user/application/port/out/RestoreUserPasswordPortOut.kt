package com.menta.bff.devices.login.entities.user.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface RestoreUserPasswordPortOut {
    fun resolve(user: String, type: UserType): Either<ApplicationError, Unit>
}
