package com.menta.api.credibanco.domain

import java.util.UUID

data class SecretsTerminal(
    val serialCode: String,
    val id: UUID,
    val merchant: Merchant,
    val customer: Customer
) {
    data class Merchant(
        val id: UUID,
    )

    data class Customer(
        val id: UUID
    )
}
