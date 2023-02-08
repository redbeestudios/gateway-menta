package com.kiwi.api.batchcloses.hexagonal.adapter.controller.model

import com.kiwi.api.batchcloses.shared.constants.OperationType
import java.time.OffsetDateTime

data class BatchCloseRequest(
    val datetime: OffsetDateTime,
    val trace: String,
    val ticket: String,
    val batch: String,
    val terminal: Terminal,
    val hostMessage: String,
    val totals: List<Total>
) {
    data class Terminal(
        val serialCode: String,
        val softwareVersion: String
    )

    data class Total(
        val operationCode: OperationType,
        val amount: Int,
        val currency: String
    )
}
