package com.menta.api.feenicia.adapter.controller.models

import java.time.OffsetDateTime
import java.util.UUID

data class OperationResponse(
    val capture: Capture,
    val amount: Amount,
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val terminal: Terminal,
    val batch: String,
    val installments: String? = null,
    val authorization: Authorization,
    val displayMessage: DisplayMessage? = null,
    val merchant: Merchant? = null
) {
    data class Merchant(
        val id: UUID
    )

    data class Authorization(
        val code: String? = null,
        val retrievalReferenceNumber: String? = null,
        val status: Status? = null,
    )

    data class DisplayMessage(
        val useCode: String? = null,
        val message: String? = null,
    )

    data class Status(
        val code: String? = null,
        val situation: Situation
    ) {
        data class Situation(
            val id: String? = null,
            val description: String
        )
    }

    data class Capture(
        val card: Card? = null,
        val inputMode: String? = null,
        val previousTransactionInputMode: String?

    ) {
        data class Card(
            val holder: Holder? = null,
            val maskedPan: String? = null,
            val workingKey: String? = null,
            val bank: String? = null,
            val type: String? = null,
            val brand: String? = null,
            val nationality: String?
        ) {
            data class Holder(
                val name: String? = null,
                val identification: Identification?
            ) {
                data class Identification(
                    val number: String? = null,
                    val type: String
                )
            }

            override fun toString(): String {
                return "Card(maskedPan=$maskedPan, workingKey=$workingKey, bank=$bank, type=$type, brand=$brand, nationality=$nationality)"
            }
        }
    }

    data class Terminal(
        val serialCode: String? = null,
        val id: String? = null,
        val softwareVersion: String? = null,
        val features: List<String>?
    )

    data class Amount(
        val total: String? = null,
        val currency: String? = null,
        val breakdown: List<Breakdown>
    ) {
        data class Breakdown(
            val description: String? = null,
            val amount: String
        )
    }
}
