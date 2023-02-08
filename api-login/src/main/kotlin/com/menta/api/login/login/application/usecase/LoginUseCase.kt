package com.menta.api.login.login.application.usecase

import UserCredentials
import arrow.core.Either
import com.menta.api.login.login.application.port.`in`.LoginInPort
import com.menta.api.login.login.application.port.out.LoginClientOutPort
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.error.model.ApplicationError
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class LoginUseCase(
    private val loginClientOutPort: LoginClientOutPort
) : LoginInPort {

    override fun login(userCredentials: UserCredentials): Either<ApplicationError, UserAuth> =
        loginClientOutPort.login(userCredentials)
            .logRight { info("user logged: {}", it) }

    companion object : CompanionLogger()
}
