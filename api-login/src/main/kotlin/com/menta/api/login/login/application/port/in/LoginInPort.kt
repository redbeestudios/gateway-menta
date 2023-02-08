package com.menta.api.login.login.application.port.`in`

import UserCredentials
import arrow.core.Either
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.error.model.ApplicationError

interface LoginInPort {
    fun login(userCredentials: UserCredentials): Either<ApplicationError, UserAuth>
}
