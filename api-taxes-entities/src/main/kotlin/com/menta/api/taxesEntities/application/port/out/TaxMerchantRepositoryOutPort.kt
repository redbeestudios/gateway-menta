package com.menta.api.taxesEntities.application.port.out

import arrow.core.Either
import com.menta.api.taxesEntities.domain.TaxMerchant
import com.menta.api.taxesEntities.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface TaxMerchantRepositoryOutPort {
    fun findBy(merchantId: UUID): Optional<TaxMerchant>
    fun create(merchant: TaxMerchant): Either<ApplicationError, TaxMerchant>
    fun update(taxMerchant: TaxMerchant): Either<ApplicationError, TaxMerchant>
}
