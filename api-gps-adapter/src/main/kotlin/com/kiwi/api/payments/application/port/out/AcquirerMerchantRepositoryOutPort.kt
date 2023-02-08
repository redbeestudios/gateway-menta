package com.kiwi.api.payments.application.port.out

import com.kiwi.api.payments.domain.AcquirerMerchant
import java.util.UUID

interface AcquirerMerchantRepositoryOutPort {
    fun findBy(merchantId: UUID): AcquirerMerchant
}
