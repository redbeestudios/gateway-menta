package com.menta.bff.devices.login.entities.deviceFlow.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.deviceFlow.application.port.out.FindDeviceFlowPortOut
import com.menta.bff.devices.login.entities.deviceFlow.domain.DeviceFlow
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindDeviceFlowApplicationService(
    private val findDeviceFlowPortOut: FindDeviceFlowPortOut
) {

    fun findBy(terminalModel: String, userAuth: UserAuth): Either<ApplicationError, List<DeviceFlow>> =
        findDeviceFlowPortOut.findBy(terminalModel, userAuth)
            .logRight { info("device flows found: {}", it) }

    companion object : CompanionLogger()
}
