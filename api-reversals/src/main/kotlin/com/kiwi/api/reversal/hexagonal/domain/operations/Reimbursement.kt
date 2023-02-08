package com.kiwi.api.reversal.hexagonal.domain.operations

import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import com.kiwi.api.reversal.hexagonal.domain.entities.Terminal
import java.time.OffsetDateTime
import java.util.UUID

data class Annulment(
    val operationId: UUID?,
    val acquirerId: String?,
    val capture: Reimbursement.Capture,
    val amount: Reimbursement.Amount,
    val installments: String,
    val trace: String,
    val batch: String,
    val ticket: String,
    val terminal: Terminal,
    val merchant: Merchant,
    val datetime: OffsetDateTime,
    val customer: Customer
)

data class Refund(
    val operationId: UUID?,
    val acquirerId: String?,
    val capture: Reimbursement.Capture,
    val amount: Reimbursement.Amount,
    val installments: String,
    val trace: String,
    val batch: String,
    val ticket: String,
    val terminal: Terminal,
    val merchant: Merchant,
    val datetime: OffsetDateTime,
    val customer: Customer
)

class Reimbursement {
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

    data class Identification(
        val number: String,
        val type: String
    )
}
