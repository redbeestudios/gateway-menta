package com.kiwi.api.reverse.hexagonal.domain

import java.time.OffsetDateTime

data class BatchClose(
    val id: String,
    val authorization: Authorization,
    val merchant: Merchant,
    val terminal: Terminal,
    val trace: String,
    val batch: String,
    val hostMessage: String,
    val datetime: OffsetDateTime,
    val softwareVersion: String,
    val ticket: String,
    val total: Total
) {
    data class Merchant(
        val id: String
    )

    data class Total(
        val operationCode: String,
        val amount: String,
        val currency: String
    )

    data class Terminal(
        val id: String,
        val serialCode: String
    )
}