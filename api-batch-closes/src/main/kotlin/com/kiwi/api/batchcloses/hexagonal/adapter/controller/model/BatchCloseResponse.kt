package com.kiwi.api.batchcloses.hexagonal.adapter.controller.model

import com.kiwi.api.batchcloses.shared.constants.OperationType
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
    val totals: List<Total>
) {
    data class Authorization(
        val code: String? = null,
        val displayMessage: String? = null,
        val retrievalReferenceNumber: String
    )

    data class Status(
        val code: String,
        val situation: Situation?,
    ) {
        data class Situation(
            val id: String,
            val description: String,
        )
    }

    data class Merchant(
        val id: String,
    )

    data class Terminal(
        val id: String,
        val serialCode: String,
        val softwareVersion: String
    )

    data class Total(
        val operationCode: OperationType,
        val amount: Int,
        val currency: String
    )
}
