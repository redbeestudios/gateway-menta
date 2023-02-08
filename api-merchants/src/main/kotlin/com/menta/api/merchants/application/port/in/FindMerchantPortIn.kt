package com.menta.api.merchants.application.port.`in`

import arrow.core.Either
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface FindMerchantPortIn {
    fun execute(merchantId: UUID): Either<ApplicationError, Merchant>
    fun findByUnivocity(taxType: String, taxId: String): Optional<Merchant>
}
