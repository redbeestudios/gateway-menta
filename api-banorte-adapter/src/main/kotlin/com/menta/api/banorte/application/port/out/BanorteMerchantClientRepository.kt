package com.menta.api.banorte.application.port.out

import arrow.core.Either
import com.menta.api.banorte.domain.BanorteMerchant
import com.menta.api.banorte.shared.error.model.ApplicationError

interface BanorteMerchantClientRepository {
    fun findBy(merchantId: String): Either<ApplicationError, BanorteMerchant>
}
