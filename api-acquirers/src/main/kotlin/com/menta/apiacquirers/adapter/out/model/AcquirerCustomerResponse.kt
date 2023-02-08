package com.menta.apiacquirers.adapter.out.model

import java.util.UUID

data class AcquirerCustomerResponse(
    val customerId: UUID,
    val acquirerId: String,
    val code: String
)
