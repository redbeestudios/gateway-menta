package com.menta.api.users.authorities.application.port.out

import arrow.core.Either
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError

interface AssignAuthorityPortOut {
    fun assign(userAssignAuthority: UserAssignAuthority): Either<ApplicationError, Unit>
}
