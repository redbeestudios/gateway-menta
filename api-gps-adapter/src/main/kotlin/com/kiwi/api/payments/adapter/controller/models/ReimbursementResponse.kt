package com.kiwi.api.payments.adapter.controller.models

import java.time.OffsetDateTime
import java.util.UUID

data class ReimbursementResponse(
    val capture: Capture,
    val amount: Amount,
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val terminal: Terminal,
    val batch: String,
    val installments: String,
    val authorization: Authorization,
    val displayMessage: DisplayMessage?,
    val merchant: Merchant
) {
    data class Merchant(
        val id: UUID
    )

    data class Authorization(
        val code: String?,
        val retrievalReferenceNumber: String?,
        val status: Status,
    )

    data class DisplayMessage(
        val useCode: String,
        val message: String,
    )

    data class Status(
        val code: String,
        val situation: Situation
    ) {
        data class Situation(
            val id: String,
            val description: String
        )
    }

    data class Capture(
        val card: Card,
        val inputMode: String,
        val previousTransactionInputMode: String?

    ) {
        data class Card(
            val holder: Holder,
            val iccData: String?,
            val maskedPan: String?,
            val workingKey: String?,
            val bank: String?,
            val type: String,
            val brand: String,
            val nationality: String?
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
        }
    }

    data class Terminal(
        val serialCode: String,
        val id: UUID,
        val softwareVersion: String,
        val features: List<String>?
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
