package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.model

import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType
import java.time.OffsetDateTime
import java.util.UUID

data class BatchCloseRequest(
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val batch: String,
    val terminal: Terminal,
    val totals: List<Total>
) {
    data class Terminal(
        val id: UUID,
        val softwareVersion: String
    )

    data class Total(
        val operationCode: OperationType,
        val amount: String,
        val currency: String
    )
}
