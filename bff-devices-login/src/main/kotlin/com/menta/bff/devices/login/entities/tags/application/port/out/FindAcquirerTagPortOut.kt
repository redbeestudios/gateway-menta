package com.menta.bff.devices.login.entities.tags.application.port.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.tags.domain.AcquirerTag
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import java.util.UUID

interface FindAcquirerTagPortOut {
    fun findBy(customerId: UUID, type: String, userAuth: UserAuth): Either<ApplicationError, List<AcquirerTag>>
}
