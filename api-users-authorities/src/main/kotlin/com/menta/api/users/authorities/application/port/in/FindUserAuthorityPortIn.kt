package com.menta.api.users.authorities.application.port.`in`

import arrow.core.Either
import com.menta.api.users.authorities.domain.UserAuthority
import com.menta.api.users.authorities.domain.UserType
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError

interface FindUserAuthorityPortIn {
    fun execute(type: UserType): Either<ApplicationError, UserAuthority>
}
