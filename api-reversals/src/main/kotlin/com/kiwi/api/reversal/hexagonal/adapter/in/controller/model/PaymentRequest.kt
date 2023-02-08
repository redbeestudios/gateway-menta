package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model

import com.kiwi.api.reversal.hexagonal.domain.operations.InputMode
import java.time.OffsetDateTime
import java.util.UUID

data class PaymentRequest(
    val operationId: UUID?,
    val acquirerId: String?,
    val capture: Capture,
    val amount: Amount,
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val terminal: Terminal,
    val batch: String,
    val installments: String
) {
    data class Capture(
        val card: Card,
        val inputMode: InputMode,
        val previousTransactionInputMode: String?
    ) {
        data class Card(
            val holder: Holder,
            val pan: String?,
            val expirationDate: String?,
            val cvv: String?,
            val track1: String?,
            val track2: String?,
            val iccData: String?,
            val cardSequenceNumber: String?,
            val bank: String?,
            val type: String,
            val brand: String,
            val pin: String?,
            val ksn: String?
        ) {
            data class Holder(
                val name: String,
                val identification: Identification?
            ) {
                data class Identification(
                    val number: String,
                    val type: String
                )
            }

            override fun toString(): String {
                return "Card(cardSequenceNumber=$cardSequenceNumber, bank=$bank, type='$type', brand='$brand')"
            }
        }
    }

    data class Amount(
        val total: String,
        val currency: String,
        val breakdown: List<Breakdown>
    ) {
        data class Breakdown(
            val description: String,
            val amount: String
        )
    }

    data class Terminal(
        val id: UUID,
        val softwareVersion: String
    )
}
