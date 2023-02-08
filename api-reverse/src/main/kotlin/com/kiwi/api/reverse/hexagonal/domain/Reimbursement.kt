package com.kiwi.api.reverse.hexagonal.domain

import java.time.OffsetDateTime

data class Annulment(
    val id: String,
    val paymentId: String,
    val authorization: Authorization,
    val capture: Capture,
    val amount: Amount,
    val installments: String,
    val trace: String,
    val batch: String,
    val hostMessage: String,
    val ticket: String,
    val terminal: Terminal,
    val merchant: Merchant,
    val datetime: OffsetDateTime
)

data class Refund(
    val id: String,
    val paymentId: String,
    val authorization: Authorization,
    val capture: Capture,
    val amount: Amount,
    val installments: String,
    val trace: String,
    val batch: String,
    val hostMessage: String,
    val ticket: String,
    val terminal: Terminal,
    val merchant: Merchant,
    val datetime: OffsetDateTime
)

data class Merchant(
    val id: String
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

data class Terminal(
    val id: String,
    val serialCode: String
)

data class Capture(
    val card: Card,
    val inputMode: String,
)

data class Identification(
    val number: String,
    val type: String
)