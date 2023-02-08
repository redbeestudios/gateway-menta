package com.menta.api.merchants.acquirer.application.port.`in`

import arrow.core.Either
import com.menta.api.merchants.acquirer.domain.AcquirerMerchant
import com.menta.api.merchants.acquirer.domain.PreAcquirerMerchant
import com.menta.api.merchants.shared.error.model.ApplicationError
import java.util.Optional

interface CreateAcquirerMerchantPortIn {
    fun execute(preAcquirerMerchant: PreAcquirerMerchant, existingAcquirerMerchant: Optional<AcquirerMerchant>): Either<ApplicationError, AcquirerMerchant>
}
