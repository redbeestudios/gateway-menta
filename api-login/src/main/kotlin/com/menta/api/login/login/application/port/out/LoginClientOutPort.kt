package com.menta.api.login.login.application.port.out

import UserCredentials
import arrow.core.Either
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.error.model.ApplicationError

interface LoginClientOutPort {
    fun login(userCredentials: UserCredentials): Either<ApplicationError, UserAuth>
}
