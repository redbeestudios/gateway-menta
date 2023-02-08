package com.menta.api.users.authorities.application.port.out

import arrow.core.Either
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError

interface AssignUserAuthorityPortOut {
    fun produce(message: UserAssignAuthority): Either<ApplicationError, Unit>
}
