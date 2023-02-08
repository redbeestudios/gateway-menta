package com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.model

import com.kiwi.api.reimbursements.hexagonal.domain.InputMode
import java.time.OffsetDateTime
import java.util.UUID

data class ReimbursementResponse(
    val id: String,
    val acquirerId: String,
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
    val hostMessage: String?,
    val datetime: OffsetDateTime
) {
    data class Status(
        val code: String,
        val situation: Situation?
    ) {
        data class Situation(
            val id: String?,
            val description: String?
        )
    }

    data class Authorization(
        val code: String?,
        val displayMessage: String?,
        val retrievalReferenceNumber: String?
    )

    data class Merchant(
        val id: UUID
    )

    data class Terminal(
        val id: UUID,
        val serialCode: String,
        val softwareVersion: String
    )

    data class Capture(
        val card: Card,
        val inputMode: InputMode,
        val previousTransactionInputMode: String?
    ) {
        data class Card(
            val holder: Holder,
            val maskedPan: String?,
            val bank: String?,
            val type: String,
            val brand: String,
            val iccData: String?
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
                return "Card(maskedPan=$maskedPan, bank=$bank, type='$type', brand='$brand')"
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
