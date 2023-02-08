package com.menta.api.merchants.acquirer.domain

import java.util.UUID

data class PreAcquirerMerchant(
    val merchantId: UUID,
    val acquirerId: String,
    val code: String?
)
