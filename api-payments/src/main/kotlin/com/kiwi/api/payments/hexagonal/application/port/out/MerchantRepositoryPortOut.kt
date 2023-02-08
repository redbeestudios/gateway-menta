package com.kiwi.api.payments.hexagonal.application.port.out

import com.kiwi.api.payments.hexagonal.domain.Payment
import java.util.UUID

interface MerchantRepositoryPortOut {
    fun retrieve(merchantId: UUID): Payment.Merchant
}
