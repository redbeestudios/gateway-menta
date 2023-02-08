package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.ForgotPasswordUserPortIn
import com.menta.api.users.application.port.out.ForgotPasswordUserPortOut
import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ForgotPasswordUserUseCase(
    private val forgotPasswordUser: ForgotPasswordUserPortOut
) : ForgotPasswordUserPortIn {

    override fun retrieve(email: Email, type: UserType): Either<ApplicationError, Unit> =
        doForgotPassword(email, type)

    private fun doForgotPassword(email: Email, type: UserType) =
        forgotPasswordUser.retrieve(email, type)
            .logRight { info("forgot password: {}", it) }

    companion object : CompanionLogger()
}
