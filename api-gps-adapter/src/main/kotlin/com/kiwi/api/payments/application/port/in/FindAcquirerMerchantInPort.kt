package com.kiwi.api.payments.application.port.`in`

import com.kiwi.api.payments.domain.AcquirerMerchant
import java.util.UUID

interface FindAcquirerMerchantInPort {
    fun execute(merchantId: UUID): AcquirerMerchant
}
