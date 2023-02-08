package com.menta.api.transactions.adapter.`in`.model

import com.menta.api.transactions.domain.CardType
import com.menta.api.transactions.domain.Installments
import com.menta.api.transactions.domain.TransactionType
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val type: TransactionType,
    val merchantId: UUID,
    val customerId: UUID,
    val terminal: Terminal,
    val currency: String,
    val operation: OperationResponse,
    val installment: Installments,
    val amount: String?,
    val refundedAmount: String?,
    val card: Card?,
) {
    data class Terminal(
        val id: UUID,
        val serialCode: String
    )

    data class Card(
        val type: CardType?,
        val maskedPan: String?,
        val brand: String?,
        val bank: String?,
        val holder: Holder?
    ) {
        data class Holder(
            val name: String?,
            val document: String?
        )
    }
}
