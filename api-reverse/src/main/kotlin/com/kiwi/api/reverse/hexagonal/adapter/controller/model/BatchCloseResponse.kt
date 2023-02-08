package com.kiwi.api.reverse.hexagonal.adapter.controller.model

import java.time.OffsetDateTime

data class BatchCloseResponse(
    val id: String,
    val status: Status,
    val authorization: Authorization,
    val merchant: Merchant,
    val terminal: Terminal,
    val ticket: String,
    val trace: String,
    val batch: String,
    val hostMessage: String,
    val datetime: OffsetDateTime,
    val softwareVersion: String,
    val total: Total
) {
    data class Merchant(
        val id: String
    )

    data class Total(
        val operationCode : String,
        val amount: String,
        val currency: String
    )

    data class Status(
        val code: String,
        val situation: Situation? = null
    ) {
        data class Situation(
            val id: String,
            val description: String
        )
    }

    data class Authorization(
        val code: String? = null,
        val displayMessage: String? = null,
        val retrievalReferenceNumber: String
    )

    data class Terminal(
        val id: String,
        val serialCode: String
    )
}
