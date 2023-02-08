package com.menta.api.transactions.adapter.`in`.consumer.model

import java.time.OffsetDateTime
import java.util.UUID

data class CreatedBillPayment(
    val billPaymentId: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    val terminalId: UUID,
    val serialCode: String,
    val currency: String,
    val operation: Operation,
    val installmentNumber: String,
    val card: Card?
) {

    data class Operation(
        val id: UUID,
        val datetime: OffsetDateTime,
        val amount: String,
        val ticketId: Int?,
    )

    data class Card(
        val type: String?,
        val mask: String?,
        val brand: String?,
        val bank: String?,
        val holder: Holder?
    ) {
        data class Holder(
            val name: String,
            val document: String
        )
    }
}
