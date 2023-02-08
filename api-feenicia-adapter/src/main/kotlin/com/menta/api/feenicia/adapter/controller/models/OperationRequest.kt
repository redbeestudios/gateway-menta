package com.menta.api.feenicia.adapter.controller.models

import com.menta.api.feenicia.domain.Customer
import java.time.OffsetDateTime
import java.util.UUID
import javax.validation.constraints.Pattern

data class OperationRequest(
    val capture: Capture,
    val amount: Amount,
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val terminal: Terminal,
    val merchant: Merchant,
    val customer: Customer,
    val batch: String,
    val orderId: String? = null,
    @get:Pattern(
        regexp = "^(01|03|06|09|12)$",
        message = "Invalid installments"
    )
    val installments: String? = null,
    val retrievalReferenceNumber: String? = null
) {
    data class Capture(
        val card: Card,
        @get:Pattern(
            regexp = "^(STRIPE|EMV|CONTACTLESS)$",
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
            val bank: String,
            @get:Pattern(
                regexp = "^(CREDIT|DEBIT)$",
                flags = [Pattern.Flag.CASE_INSENSITIVE],
                message = "Invalid card_type"
            )
            val type: String,
            @get:Pattern(
                regexp = "^(AMERICAN EXPRESS|DINERS CLUB|MAESTRO|MASTERCARD|VISA|CREDENCIAL)$",
                flags = [Pattern.Flag.CASE_INSENSITIVE],
                message = "Invalid brand_name"
            )
            val brand: String
        ) {
            data class EMV(
                val iccData: String? = null,
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
                return "Card(track1=$track1, track2=$track2, pin=$pin, emv=$emv, bank='$bank', type='$type', brand='$brand')"
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
        val id: UUID
    )
}
