package com.menta.bff.devices.login.orchestrate.application.port.`in`

import arrow.core.Either
import com.menta.bff.devices.login.orchestrate.domain.ConfirmRestoreUserPassword
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface OrchestratedConfirmPasswordInPort {

    fun confirm(confirmRestoreUserPassword: ConfirmRestoreUserPassword): Either<ApplicationError, Unit>
}
