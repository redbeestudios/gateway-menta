package com.kiwi.api.reimbursements.hexagonal.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Annulment(
    val paymentId: UUID,
    val acquirerId: String,
    val merchant: Merchant,
    val terminal: Reimbursement.Terminal,
    val capture: Reimbursement.Capture,
    val amount: Reimbursement.Amount,
    val installments: String,
    val trace: String,
    val ticket: String,
    val batch: String,
    val datetime: OffsetDateTime,
    val customer: Customer,
)

data class Refund(
    val paymentId: UUID,
    val acquirerId: String,
    val merchant: Merchant,
    val terminal: Reimbursement.Terminal,
    val capture: Reimbursement.Capture,
    val amount: Reimbursement.Amount,
    val installments: String,
    val trace: String,
    val ticket: String,
    val batch: String,
    val datetime: OffsetDateTime,
    val customer: Customer,
)

class Reimbursement {
    data class Terminal(
        val id: UUID,
        val merchantId: UUID,
        val customerId: UUID,
        val serialCode: String,
        val hardwareVersion: String,
        val softwareVersion: String,
        val tradeMark: String,
        val model: String,
        val status: String,
        val features: List<Feature>
    )

    data class Capture(
        val card: Card,
        val inputMode: InputMode,
        val previousTransactionInputMode: String?,
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
}
