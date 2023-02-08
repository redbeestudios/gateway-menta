package com.menta.api.transactions.domain

import java.util.UUID

data class Transaction(
    val id: UUID,
    val type: TransactionType,
    val merchantId: UUID,
    val customerId: UUID,
    val terminal: Terminal,
    val currency: String,
    val operation: Operation,
    val installment: Installments,
    val amount: String?,
    val refundedAmount: String?,
    val card: Card?,
) {
    data class Terminal(
        val id: UUID,
        val serialCode: String
    )
}
