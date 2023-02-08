package com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.model

import com.kiwi.api.reimbursements.hexagonal.domain.InputMode
import java.time.OffsetDateTime
import java.util.UUID

data class ReimbursementRequest(
    val paymentId: UUID,
    val acquirerId: String,
    val datetime: OffsetDateTime,
    val capture: Capture,
    val amount: Amount,
    val trace: String,
    val ticket: String,
    val batch: String,
    val installments: String,
    val terminal: Terminal,
) {

    data class Capture(
        val card: Card,
        val inputMode: InputMode,
        val previousTransactionInputMode: String?
    ) {
        data class Card(
            val holder: Holder,
            val iccData: String?,
            val cardSequenceNumber: String?,
            val bank: String?,
            val type: String,
            val brand: String,
            val pin: String?,
            val ksn: String?,
            val pan: String?,
            val cvv: String?,
            val expirationDate: String?,
            val track1: String?,
            val track2: String?
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

    data class Terminal(
        val id: UUID,
        val softwareVersion: String,
    )

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
}
