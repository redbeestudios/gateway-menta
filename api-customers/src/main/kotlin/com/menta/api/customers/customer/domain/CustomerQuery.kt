package com.menta.api.customers.customer.domain

import java.util.UUID

data class CustomerQuery(
    val id: UUID?,
    val country: Country?,
    val status: Status?,
    val createDate: String?
)
