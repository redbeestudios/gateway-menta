package com.menta.apiacquirers.domain

import java.util.UUID

data class AcquirerCustomer(
    val customerId: UUID,
    val acquirers: List<Acquirer>
) {

    data class Acquirer(
        val acquirerId: String,
        val code: String
    )
}
