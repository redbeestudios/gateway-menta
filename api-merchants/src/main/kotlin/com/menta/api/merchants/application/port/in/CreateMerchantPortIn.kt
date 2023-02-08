package com.menta.api.merchants.application.port.`in`

import arrow.core.Either
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.domain.PreMerchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import java.util.Optional

interface CreateMerchantPortIn {
    fun execute(preMerchant: PreMerchant, existingMerchant: Optional<Merchant>): Either<ApplicationError, Merchant>
}
