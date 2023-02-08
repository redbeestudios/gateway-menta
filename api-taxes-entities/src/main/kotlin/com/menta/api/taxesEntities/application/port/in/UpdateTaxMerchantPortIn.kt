package com.menta.api.taxesEntities.application.port.`in`

import arrow.core.Either
import com.menta.api.taxesEntities.domain.PreTaxMerchant
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import java.util.UUID

interface UpdateTaxMerchantPortIn {
    fun execute(preTaxMerchant: PreTaxMerchant, merchantId: UUID): Either<ApplicationError, TaxMerchant>
}
