package com.menta.bff.devices.login.entities.user.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.user.domain.User
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface FindUserPortOut {
    fun findBy(email: Email, type: UserType): Either<ApplicationError, User>
}
