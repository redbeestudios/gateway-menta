package com.menta.api.customers.acquirer.domain

import java.util.UUID

data class PreAcquirerCustomer(
    val customerId: UUID,
    val acquirerId: String,
    val code: String
)
