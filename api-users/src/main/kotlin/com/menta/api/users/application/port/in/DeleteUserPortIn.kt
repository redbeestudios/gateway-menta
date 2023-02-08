package com.menta.api.users.application.port.`in`

import arrow.core.Either
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.error.model.ApplicationError

interface DeleteUserPortIn {
    fun deleteBy(email: Email, type: UserType): Either<ApplicationError, Unit>
}
