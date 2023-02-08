package com.menta.bff.devices.login.entities.user.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface ConfirmUserPasswordPortOut {
    fun confirm(confirmRestoreUserPassword: ConfirmRestoreUserPassword): Either<ApplicationError, Unit>
}
