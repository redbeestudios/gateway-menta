package com.menta.api.customers.acquirer.adapter.`in`.model

import java.time.OffsetDateTime

data class AcquirerCustomerResponse(
    val customerId: String,
    val acquirerId: String,
    val code: String,
    val createDate: OffsetDateTime,
    val updateDate: OffsetDateTime
)
