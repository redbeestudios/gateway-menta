package com.kiwi.api.batchcloses.hexagonal.domain

import com.kiwi.api.batchcloses.shared.constants.OperationType
import java.time.OffsetDateTime

data class BatchClose(
    val id: String,
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
