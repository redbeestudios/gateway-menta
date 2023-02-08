package com.menta.api.users.application.port.`in`

import arrow.core.Either
import com.menta.api.users.domain.NewUser
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.error.model.ApplicationError

interface CreateUserPortIn {
    fun create(newUser: NewUser): Either<ApplicationError, User>
}
