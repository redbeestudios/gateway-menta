package com.kiwi.api.reversal.hexagonal.adapter.out.rest.model

import com.kiwi.api.reversal.hexagonal.domain.operations.InputMode
import java.time.OffsetDateTime

data class AcquirerResponse(
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
    val merchant: Merchant,
    // val hostMessage: String?
) {
    data class Merchant(
        val id: String
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
        val inputMode: InputMode,
        val previousTransactionInputMode: String?

    ) {
        data class Card(
            val holder: Holder,
            val maskedPan: String?,
            val workingKey: String?,
            val bank: String?,
            val type: String,
            val iccData: String?,
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

            override fun toString(): String {
                return "Card(maskedPan=$maskedPan, workingKey=$workingKey, bank=$bank, type='$type', brand='$brand', nationality=$nationality)"
            }
        }
    }

    data class Terminal(
        val serialCode: String,
        val id: String,
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
