package com.menta.api.users.authorities.application.port.`in`

import arrow.core.Either
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError

interface AssignAuthorityPortIn {
    fun assign(userAssignAuthority: UserAssignAuthority): Either<ApplicationError, Unit>
}
