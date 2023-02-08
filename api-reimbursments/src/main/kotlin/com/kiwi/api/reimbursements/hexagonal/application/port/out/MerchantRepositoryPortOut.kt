package com.kiwi.api.reimbursements.hexagonal.application.port.out

import com.kiwi.api.reimbursements.hexagonal.domain.Merchant
import java.util.UUID

interface MerchantRepositoryPortOut {
    fun retrieve(merchantId: UUID): Merchant
}
