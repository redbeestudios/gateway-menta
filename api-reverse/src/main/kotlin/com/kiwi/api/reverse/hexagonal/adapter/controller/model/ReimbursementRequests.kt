package com.kiwi.api.reverse.hexagonal.adapter.controller.model

import java.time.OffsetDateTime

data class ReimbursementRequest(
    val paymentId: String,
    val capture: Capture,
    val amount: Amount,
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val terminal: Terminal,
    val batch: String,
    val installments: String,
    val hostMessage: String,
) {
    data class Capture(
        val card: Card,
        val inputMode: String,
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
            val bank: String,
            val type: String,
            val brand: String
        ) {
            data class Holder(
                val name: String,
                val identification: Identification
            ) {
                data class Identification(
                    val number: String,
                    val type: String
                )
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
        val serialCode: String
    )
}
