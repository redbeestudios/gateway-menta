package com.kiwi.api.payments.domain

import java.util.UUID

data class Customer(
    val id: UUID,
    val country: Country,
    val businessName: String,
    val fantasyName: String,
    val tax: Tax,
    val activity: String,
    val address: Address,
    val status: StatusCustomer
) {
    data class Tax(
        val type: String,
        val id: String
    )
}
