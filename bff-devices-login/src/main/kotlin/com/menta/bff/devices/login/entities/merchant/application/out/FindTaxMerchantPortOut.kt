package com.menta.bff.devices.login.entities.merchant.application.out

import arrow.core.Either
import com.menta.bff.devices.login.entities.merchant.domain.taxes.TaxMerchant
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import java.util.UUID

interface FindTaxMerchantPortOut {
    fun findBy(merchantId: UUID): Either<ApplicationError, TaxMerchant>
}
