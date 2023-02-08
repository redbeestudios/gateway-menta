package com.menta.api.merchants.acquirer.application.port.out

import arrow.core.Either
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import java.util.Optional
import java.util.UUID

interface AcquirerMerchantRepositoryOutPort {
    fun findBy(acquirer: String, merchantId: UUID): Optional<AcquirerMerchant>
    fun create(acquirerMerchant: AcquirerMerchant): AcquirerMerchant
    fun update(acquirerMerchant: AcquirerMerchant): Either<ApplicationError, AcquirerMerchant>
}
