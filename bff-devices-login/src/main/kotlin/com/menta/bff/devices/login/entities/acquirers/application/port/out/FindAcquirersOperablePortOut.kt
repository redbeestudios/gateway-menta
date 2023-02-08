package com.menta.bff.devices.login.entities.acquirers.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.acquirers.domain.AcquirerOperable
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import java.util.UUID

interface FindAcquirersOperablePortOut {
    fun findBy(customerId: UUID, userAuth: UserAuth): Either<ApplicationError, List<AcquirerOperable>>
}
