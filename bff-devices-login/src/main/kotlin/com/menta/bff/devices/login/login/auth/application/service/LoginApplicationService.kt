package com.menta.bff.devices.login.login.auth.application.service

import arrow.core.Either
import com.menta.bff.devices.login.login.auth.application.port.out.LoginPortOut
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserCredentials
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class LoginApplicationService(
    private val loginPortOut: LoginPortOut
) {

    fun login(credentials: UserCredentials): Either<ApplicationError, UserAuth> =
        loginPortOut.login(credentials)
            .logRight { info("user logged in: {}", it) }

    companion object : CompanionLogger()
}
