package com.menta.api.banorte.application.port.`in`

import arrow.core.Either
import com.menta.api.banorte.domain.BanorteMerchant
import com.menta.api.banorte.shared.error.model.ApplicationError

interface FindBanorteMerchantInPort {
    fun execute(merchantId: String): Either<ApplicationError, BanorteMerchant>
}
