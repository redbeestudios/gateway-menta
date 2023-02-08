package com.menta.api.users.application.usecase

import arrow.core.Either
import com.menta.api.users.application.port.`in`.SetUserPasswordPortIn
import com.menta.api.users.application.port.out.SetUserPasswordPortOut
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class SetUserPasswordUseCase(
    private val setUserPasswordPortOut: SetUserPasswordPortOut
) : SetUserPasswordPortIn {

    override fun setPassword(setUserPassword: SetUserPassword): Either<ApplicationError, Unit> =
        setUserPasswordPortOut.setPassword(setUserPassword)
            .logRight { info("set user password: {}", it) }

    companion object : CompanionLogger()
}
