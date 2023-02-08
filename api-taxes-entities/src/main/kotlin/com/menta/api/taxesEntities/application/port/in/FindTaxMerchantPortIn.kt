package com.menta.api.taxesEntities.application.port.`in`

import arrow.core.Either
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import java.util.UUID

interface FindTaxMerchantPortIn {
    fun execute(merchantId: UUID): Either<ApplicationError, TaxMerchant>
}
