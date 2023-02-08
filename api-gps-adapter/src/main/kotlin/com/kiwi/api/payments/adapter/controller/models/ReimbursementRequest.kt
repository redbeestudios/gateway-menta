package com.kiwi.api.payments.adapter.controller.models

import com.kiwi.api.payments.domain.Customer
import com.kiwi.api.payments.domain.Merchant
import com.kiwi.api.payments.domain.Terminal
import java.time.OffsetDateTime
import javax.validation.constraints.Pattern

data class ReimbursementRequest(
    val capture: Capture,
    val amount: Amount,
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val terminal: Terminal,
    val merchant: Merchant,
    val customer: Customer,
    val batch: String,
    val installments: String,
    val retrievalReferenceNumber: String
) {
    data class Capture(
        val card: Card,
        @get:Pattern(
            regexp = "^(MANUAL|STRIPE|EMV|CONTACTLESS|ECOMMERCE|ONFILE)$",
            message = "Invalid input mode"
        )
        val inputMode: String,
        @get:Pattern(
            regexp = "^(NO_CHIP|CHIP|FALLBACK)$",
            message = "Invalid previous transaction input mode"
        )
        val previousTransactionInputMode: String?

    ) {
        data class Card(
            val holder: Holder,
            @get:Pattern(
                regexp = "\\d{1,20}",
                message = "Invalid pan"
            )
            val pan: String?,
            @get:Pattern(
                regexp = "\\d{4}",
                message = "Invalid expirationDate"
            )
            val expirationDate: String? = null,
            @get:Pattern(
                regexp = "\\d{3,4}",
                message = "Invalid cvv"
            )
            val cvv: String? = null,
            val track1: String? = null,
            @Pattern(regexp = "^[0-9]*$")
            val track2: String? = null,
            val pin: String?,
            val emv: EMV? = null,
            val bank: String?,
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
                @Pattern(regexp = "^[0-9]*$")
                val iccData: String? = null,
                val cardSequenceNumber: String? = null,
                val ksn: String? = null,
            ) {
                override fun toString(): String {
                    return "Card(cardSequenceNumber=$cardSequenceNumber)"
                }
            }

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
                return "Card(emv=$emv, bank=$bank, type='$type', brand='$brand')"
            }
        }
    }

    data class Amount(
        @get:Pattern(
            regexp = "\\d{1,12}",
            message = "Invalid total"
        )
        val total: String,
        @get:Pattern(
            regexp = "^(USD|ARS|UYU)$",
            flags = [Pattern.Flag.CASE_INSENSITIVE],
            message = "Invalid currency"
        )
        val currency: String,
        val breakdown: List<Breakdown>
    ) {
        data class Breakdown(
            @get:Pattern(
                regexp = "^(OPERATION|TIP|ADVANCE)$",
                flags = [Pattern.Flag.CASE_INSENSITIVE],
                message = "Invalid description"
            )
            val description: String,
            val amount: String
        )
    }
}
