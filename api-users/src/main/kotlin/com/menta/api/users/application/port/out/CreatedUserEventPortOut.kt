package com.menta.api.users.application.port.out

import arrow.core.Either
import com.menta.api.users.domain.User
import com.menta.api.users.shared.other.error.model.ApplicationError

interface CreatedUserEventPortOut {

    fun produce(createdUser: User): Either<ApplicationError, Unit>

}
