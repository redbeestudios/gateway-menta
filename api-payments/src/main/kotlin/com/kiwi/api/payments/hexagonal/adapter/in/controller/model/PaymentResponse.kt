package com.kiwi.api.payments.hexagonal.adapter.`in`.controller.model

import com.kiwi.api.payments.hexagonal.domain.InputMode
import com.kiwi.api.payments.hexagonal.domain.StatusCode
import java.time.OffsetDateTime
import java.util.UUID

data class PaymentResponse(
    val id: UUID,
    val ticketId: Int?,
    val status: Status,
    val authorization: Authorization,
    val merchant: Merchant,
    val terminal: Terminal,
    val capture: Capture,
    val amount: Amount,
    val installments: String,
    val trace: String,
    val batch: String,
    val hostMessage: String?,
    val ticket: String,
    val datetime: OffsetDateTime
) {
    data class Merchant(
        val id: String
    )

    data class Status(
        val code: StatusCode,
        val situation: Situation?
    ) {
        data class Situation(
            val id: String,
            val description: String
        )
    }

    data class Authorization(
        val code: String?,
        val displayMessage: String?,
        val retrievalReferenceNumber: String?
    )

    data class Terminal(
        val id: String,
        val serialCode: String,
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

    data class Capture(
        val card: Card,
        val inputMode: InputMode,
        val previousTransactionInputMode: String?
    ) {
        data class Card(
            val holder: Holder,
            val iccData: String?,
            val maskedPan: String?,
            val bank: String?,
            val type: String,
            val brand: String
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
}
