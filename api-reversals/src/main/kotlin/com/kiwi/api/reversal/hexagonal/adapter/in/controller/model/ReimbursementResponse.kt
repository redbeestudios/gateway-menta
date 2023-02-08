package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model

import com.kiwi.api.reversal.hexagonal.domain.operations.InputMode
import java.time.OffsetDateTime

data class ReimbursementResponse(
    val operationId: String?,
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
    data class Merchant(
        val id: String
    )

    data class Terminal(
        val id: String,
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
            val iccData: String?,
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
