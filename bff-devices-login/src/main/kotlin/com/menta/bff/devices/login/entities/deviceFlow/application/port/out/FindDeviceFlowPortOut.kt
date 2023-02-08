package com.menta.bff.devices.login.entities.deviceFlow.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError

interface FindDeviceFlowPortOut {
    fun findBy(terminalModel: Email, userAuth: UserAuth): Either<ApplicationError, List<DeviceFlow>>
}
