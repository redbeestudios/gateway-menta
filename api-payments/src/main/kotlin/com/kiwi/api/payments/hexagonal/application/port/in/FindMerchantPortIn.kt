package com.kiwi.api.payments.hexagonal.application.port.`in`

import com.kiwi.api.payments.hexagonal.domain.Payment
import java.util.UUID

interface FindMerchantPortIn {
    fun execute(merchantId: UUID): Payment.Merchant
}
