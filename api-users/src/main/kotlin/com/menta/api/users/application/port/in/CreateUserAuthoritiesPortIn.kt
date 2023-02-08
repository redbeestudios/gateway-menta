package com.menta.api.users.application.port.`in`

import arrow.core.Either
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.error.model.ApplicationError

interface CreateUserAuthoritiesPortIn {
    fun execute(createdUser: User): Either<ApplicationError, Unit>
}
