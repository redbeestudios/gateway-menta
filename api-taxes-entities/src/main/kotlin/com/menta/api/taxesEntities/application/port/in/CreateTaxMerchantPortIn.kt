package com.menta.api.taxesEntities.application.port.`in`

import arrow.core.Either
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApplicationError

interface CreateTaxMerchantPortIn {
    fun execute(taxMerchant: TaxMerchant): Either<ApplicationError, TaxMerchant>
}
