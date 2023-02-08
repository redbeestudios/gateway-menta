package com.kiwi.api.reverse.hexagonal.adapter.controller.model

import java.time.OffsetDateTime

data class BatchCloseRequest(
    val datetime: OffsetDateTime,
    val trace: String,
    val terminal: Terminal,
    val softwareVersion: String,
    val total: Total,
    val batch: String,
    val hostMessage: String,
    val ticket: String
) {
    data class Terminal(
        val serialCode: String
    )
    data class Total(
        val operationCode : String,
        val amount: String,
        val currency: String
    )
}
