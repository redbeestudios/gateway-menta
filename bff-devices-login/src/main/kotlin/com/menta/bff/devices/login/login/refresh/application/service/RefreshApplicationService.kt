package com.menta.bff.devices.login.login.refresh.application.service

import arrow.core.Either
import com.menta.bff.devices.login.login.refresh.application.port.out.RefreshPortOut
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class RefreshApplicationService(
    private val refreshOutPort: RefreshPortOut
) {

    fun refresh(refresh: Refresh): Either<ApplicationError, UserAuth> =
        refreshOutPort.refresh(refresh)
            .logRight { info("user refresh token in: {}", it) }

    companion object : CompanionLogger()
}
