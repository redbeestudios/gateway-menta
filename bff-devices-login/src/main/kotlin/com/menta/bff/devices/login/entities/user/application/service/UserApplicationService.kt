package com.menta.bff.devices.login.entities.user.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.user.application.port.out.ConfirmUserPasswordPortOut
import com.menta.bff.devices.login.entities.user.application.port.out.FindUserPortOut
import com.menta.bff.devices.login.entities.user.application.port.out.RestoreUserPasswordPortOut
import com.menta.bff.devices.login.entities.user.domain.User
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class UserApplicationService(
    private val findUserPortOut: FindUserPortOut,
    private val restoreUserPasswordPortOut: RestoreUserPasswordPortOut,
    private val confirmUserPasswordPortOut: ConfirmUserPasswordPortOut
) {

    fun find(email: Email, type: UserType): Either<ApplicationError, User> =
        findUserPortOut.findBy(email, type)
            .logRight { info("user found: {}", it) }

    fun renew(email: Email, type: UserType): Either<ApplicationError, Unit> =
        restoreUserPasswordPortOut.resolve(email, type)
            .logRight { info("user password restore successfully") }

    fun confirmPassword(confirmRestoreUserPassword: ConfirmRestoreUserPassword) =
        confirmUserPasswordPortOut.confirm(confirmRestoreUserPassword)
            .logRight { info("user password confirm successfully") }

    companion object : CompanionLogger()
}
