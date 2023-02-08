package com.menta.api.users.application.port.out

import arrow.core.Either
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.error.model.ApplicationError

interface FindUserPortOut {
    fun findBy(email: Email, type: UserType): Either<ApplicationError, User>
}
