package com.kiwi.api.reimbursements.hexagonal.application.port.`in`

import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
import java.util.UUID

interface FindMerchantPortIn {
    fun execute(merchantId: UUID): Merchant
}
