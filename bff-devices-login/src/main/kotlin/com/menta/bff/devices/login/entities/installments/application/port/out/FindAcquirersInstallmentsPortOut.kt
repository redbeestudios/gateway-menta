package com.menta.bff.devices.login.entities.installments.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.installments.domain.AcquirerInstallment
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import java.util.UUID

interface FindAcquirersInstallmentsPortOut {
    fun findBy(merchantId: UUID, userAuth: UserAuth): Either<ApplicationError, List<AcquirerInstallment>>
}
