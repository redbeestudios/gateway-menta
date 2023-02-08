package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.ConfirmForgotPasswordUserPortIn
import com.menta.api.users.application.port.out.ConfirmPasswordForgotUserPortOut
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ConfirmForgotPasswordUserUseCase(
    private val confirmForgotPasswordUser: ConfirmPasswordForgotUserPortOut
) : ConfirmForgotPasswordUserPortIn {

    override fun confirm(confirmForgotPasswordUser: ConfirmForgotPasswordUser): Either<ApplicationError, Unit> =
        confirmForgotPasswordUser
            .doConfirmForgotPassword()

    private fun ConfirmForgotPasswordUser.doConfirmForgotPassword() =
        confirmForgotPasswordUser.confirm(this)
            .logRight { info("confirm forgot password: {}", it) }

    companion object : CompanionLogger()
}
