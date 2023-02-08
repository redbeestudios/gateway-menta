package com.menta.apisecrets.domain

import java.util.UUID

data class Terminal(
    val serialCode: String,
    val id: UUID,
    val merchant: Merchant
) {
    data class Merchant(
        val id: UUID,
        val customer: Customer
    ) {
        data class Customer(
            val id: UUID
        )
    }
}
