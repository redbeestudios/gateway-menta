package com.menta.api.merchants.acquirer.adapter.`in`.model

import java.time.OffsetDateTime
import java.util.UUID

data class AcquirerMerchantResponse(
    val merchantId: UUID,
    val acquirer: String,
    val code: String,
    val createDate: OffsetDateTime,
    val updateDate: OffsetDateTime
)
