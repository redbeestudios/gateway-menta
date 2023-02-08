package com.kiwi.api.payments.hexagonal.adapter.`in`.controller.model

import com.kiwi.api.payments.hexagonal.domain.InputMode
import java.time.OffsetDateTime
import java.util.UUID

data class PaymentRequest(
    val capture: Capture,
    val amount: Amount,
    val trace: String,
    val ticket: String,
    val batch: String,
    val installments: String,
    val terminal: Terminal,
    val datetime: OffsetDateTime
) {
    data class Capture(
        val card: Card,
        val inputMode: InputMode,
        val previousTransactionInputMode: String? = null
    ) {
        data class Card(
            val holder: Holder,
            val pan: String? = null,
            val expirationDate: String? = null,
            val cvv: String? = null,
            val track1: String? = null,
            val track2: String? = null,
            val iccData: String? = null,
            val cardSequenceNumber: String? = null,
            val bank: String? = null,
            val type: String,
            val brand: String,
            val pin: String?,
            val ksn: String?
        ) {
            data class Holder(
                val name: String,
                val identification: Identification? = null
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
