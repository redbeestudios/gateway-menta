package com.menta.api.users.application.port.out

import arrow.core.Either
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.shared.other.error.model.ApplicationError

interface ConfirmPasswordForgotUserPortOut {
    fun confirm(confirmForgotPasswordUser: ConfirmForgotPasswordUser): Either<ApplicationError, Unit>
}
