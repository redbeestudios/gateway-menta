package com.menta.api.banorte.adapter.controller.models

import com.menta.api.banorte.domain.CardBrand
import java.time.OffsetDateTime
import javax.validation.Valid
import javax.validation.constraints.Pattern

data class OperationRequest(
    @get:Valid
    val capture: Capture,
    val amount: Amount,
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val terminal: Terminal,
    val merchant: Merchant,
    val batch: String,
    val installments: String,
    val retrievalReferenceNumber: String?
) {
    data class Capture(
        val card: Card,
        @get:Pattern(
            regexp = "^(CHIP|CONTACTLESS|MAG_STRIPE)$",
            flags = [Pattern.Flag.CASE_INSENSITIVE],
            message = "Invalid input_mode"
        )
        val inputMode: String,
        val previousTransactionInputMode: String?

    ) {
        data class Card(
            val holder: Holder,
            val pan: String,
            val expirationDate: String? = null,
            val cvv: String? = null,
            val track1: String? = null,
            val track2: String? = null,
            val pin: String?,
            val emv: EMV? = null,
            val bank: String?,
            val type: String,
            @get:Pattern(
                regexp = "^(AMEX|VISA|MASTER)$",
                flags = [Pattern.Flag.CASE_INSENSITIVE],
                message = "Invalid brand name"
            )
            val brand: CardBrand
        ) {
            data class EMV(
                val iccData: String?,
                val cardSequenceNumber: String? = null,
                val ksn: String? = null,
            )

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
                return "Card(track1=$track1, track2=$track2, pin=$pin, emv=$emv, bank=$bank, type='$type', brand=$brand)"
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
        val id: String,
        val serialCode: String,
        val softwareVersion: String,
        val features: List<String>
    )

    data class Merchant(
        val id: String
    )
}
