package com.kiwi.api.reverse.hexagonal.adapter.controller.model

import java.time.OffsetDateTime

data class ReimbursementResponse(
    val id: String,
    val paymentId: String,
    val status: Status,
    val authorization: Authorization,
    val merchant: Merchant,
    val terminal: Terminal,
    val capture: Capture,
    val amount: Amount,
    val installments: String,
    val ticket: String,
    val trace: String,
    val batch: String,
    val hostMessage: String,
    val datetime: OffsetDateTime,
) {
    data class Merchant(
        val id: String
    )

    data class Status(
        val code: String,
        val situation: Situation? = null
    ) {
        data class Situation(
            val id: String,
            val description: String
        )
    }

    data class Authorization(
        val code: String? = null,
        val displayMessage: String? = null,
        val retrievalReferenceNumber: String
    )

    data class Terminal(
        val id: String,
        val serialCode: String
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

    data class Capture(
        val card: Card,
        val inputMode: String,
    ) {
        data class Card(
            val holder: Holder,
            val maskedPan: String,
            val iccData: String?,
            val cardSequenceNumber: String?,
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
}
