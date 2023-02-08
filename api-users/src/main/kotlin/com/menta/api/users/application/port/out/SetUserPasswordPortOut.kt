package com.menta.api.users.application.port.out

import arrow.core.Either
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.shared.other.error.model.ApplicationError

interface SetUserPasswordPortOut {
    fun setPassword(setUserPassword: SetUserPassword): Either<ApplicationError, Unit>
}
