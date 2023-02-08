package com.menta.bff.devices.login.entities.installments.application.service

import arrow.core.Either
import com.menta.bff.devices.login.entities.installments.application.port.out.FindAcquirersInstallmentsPortOut
import com.menta.bff.devices.login.entities.installments.domain.AcquirerInstallment
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindAcquirersInstallmentsApplicationService(
    private val findInstallments: FindAcquirersInstallmentsPortOut
) {

    fun findBy(merchantId: UUID, userAuth: UserAuth): Either<ApplicationError, List<AcquirerInstallment>> =
        findInstallments.findBy(merchantId, userAuth)
            .logRight { info("acquirers installments found: {}", it) }

    companion object : CompanionLogger()
}
