package com.kiwi.api.payments.adapter.controller.models

import com.kiwi.api.payments.domain.Customer
import com.kiwi.api.payments.domain.Merchant
import com.kiwi.api.payments.domain.Terminal
import java.time.OffsetDateTime
import javax.validation.Valid
import javax.validation.constraints.Pattern

data class OperationRequest(
    @get:Valid
    val capture: Capture,

    @get:Valid
    val amount: Amount,

    val datetime: OffsetDateTime,

    @get:Pattern(
        regexp = "\\d{1,6}",
        message = "Invalid trace"
    )
    val trace: String,

    @get:Pattern(
        regexp = "\\d{1,4}",
        message = "Invalid ticket"
    )
    val ticket: String,

    @get:Valid
    val terminal: Terminal,

    @get:Valid
    val merchant: Merchant,

    @get:Valid
    val customer: Customer,

    @get:Pattern(
        regexp = "\\d{1,3}",
        message = "Invalid batch"
    )
    val batch: String,

    @get:Pattern(
        regexp = "\\d{1,2}",
        message = "Invalid installments"
    )
    val installments: String,

    @get:Pattern(
        regexp = "\\w{12}",
        message = "Invalid retrieval_reference_number"
    )
    val retrievalReferenceNumber: String?
) {
    data class Capture(
        @get:Valid
        val card: Card,

        @get:Pattern(
            regexp = "^(MANUAL|STRIPE|EMV|CONTACTLESS|ECOMMERCE|ONFILE)$",
            message = "Invalid input_mode"
        )
        val inputMode: String,

        @get:Pattern(
            regexp = "^(NO_CHIP|CHIP|FALLBACK)$",
            message = "Invalid previous_transaction_input_mode"
        )
        val previousTransactionInputMode: String?

    ) {
        data class Card(
            @get:Valid
            val holder: Holder,

            @get:Pattern(
                regexp = "^[0-9]*[fF]*",
                message = "Invalid pan"
            )
            val pan: String,

            @get:Pattern(
                regexp = "\\d{4}",
                message = "Invalid expiration_date"
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
                // @Pattern(regexp = "^[0-9]*$")
                val iccData: String?,
                val cardSequenceNumber: String? = null,
                val ksn: String? = null,
            ) {
                override fun toString(): String {
                    return "EMV(cardSequenceNumber='$cardSequenceNumber')"
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
                return "Card(emv=$emv, bank='$bank', type='$type', brand='$brand')"
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

            @get:Pattern(
                regexp = "\\d{1,12}",
                message = "Invalid breakdown_amount"
            )
            val amount: String
        )
    }
}
