package com.menta.api.transactions.adapter.`in`.consumer.model

import com.menta.api.transactions.domain.Authorization
import com.menta.api.transactions.domain.Payment
import com.menta.api.transactions.domain.TransactionType

data class CreatedPayment(
    val id: String,
    val ticketId: Int?,
    val origin: TransactionType?,
    val authorization: Authorization,
    val data: Payment
)
