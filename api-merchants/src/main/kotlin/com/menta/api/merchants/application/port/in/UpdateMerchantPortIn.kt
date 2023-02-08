package com.menta.api.merchants.application.port.`in`

import arrow.core.Either
import com.menta.api.merchants.domain.Merchant
import com.menta.api.merchants.shared.error.model.ApplicationError

interface UpdateMerchantPortIn {
    fun execute(merchant: Merchant): Either<ApplicationError, Merchant>
}
