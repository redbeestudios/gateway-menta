package com.kiwi.api.reversal.hexagonal.domain.operations

data class CreatedPayment(
    val id: String,
    val authorization: Authorization,
    val data: Payment
)
