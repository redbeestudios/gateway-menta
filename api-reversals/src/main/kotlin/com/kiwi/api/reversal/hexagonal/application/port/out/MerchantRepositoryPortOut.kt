package com.kiwi.api.reversal.hexagonal.application.port.out

import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import java.util.UUID

interface MerchantRepositoryPortOut {
    fun retrieve(merchantId: UUID): Merchant
}
