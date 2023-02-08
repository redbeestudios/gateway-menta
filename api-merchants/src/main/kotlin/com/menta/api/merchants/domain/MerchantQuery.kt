package com.menta.api.merchants.domain

import java.util.UUID

data class MerchantQuery(
    val merchantId: UUID?,
    val status: Status?,
    val customerId: UUID?,
    val createDate: String?
)
