package com.kiwi.api.reversal.hexagonal.application.port.`in`

import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import java.util.UUID

interface FindMerchantPortIn {
    fun execute(merchantId: UUID): Merchant
}
