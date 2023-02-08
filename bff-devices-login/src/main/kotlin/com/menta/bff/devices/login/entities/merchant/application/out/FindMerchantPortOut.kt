package com.menta.bff.devices.login.entities.merchant.application.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import java.util.UUID

interface FindMerchantPortOut {
    fun findBy(id: UUID): Either<ApplicationError, Merchant>
}
