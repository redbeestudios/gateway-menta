package com.menta.api.merchants.acquirer.application.port.`in`

import arrow.core.Either
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface FindAcquirerMerchantPortIn {
    fun execute(acquirer: String, merchantId: UUID): Either<ApplicationError, AcquirerMerchant>
    fun find(merchantId: UUID, acquirerId: String): Optional<AcquirerMerchant>
}
