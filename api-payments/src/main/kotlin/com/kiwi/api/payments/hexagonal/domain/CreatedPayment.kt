package com.kiwi.api.payments.hexagonal.domain

import java.util.UUID

data class CreatedPayment(
    val id: UUID,
    val ticketId: Int,
    val origin: Origin,
    val authorization: Authorization,
    val data: Payment
)
