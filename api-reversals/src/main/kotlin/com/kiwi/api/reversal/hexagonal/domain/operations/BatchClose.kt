package com.kiwi.api.reversal.hexagonal.domain.operations

import com.kiwi.api.reversal.hexagonal.domain.entities.Customer
import com.kiwi.api.reversal.hexagonal.domain.entities.Merchant
import com.kiwi.api.reversal.hexagonal.domain.entities.Terminal
import java.time.OffsetDateTime

data class BatchClose(
    val id: String,
    val authorization: Authorization,
    val merchant: Merchant,
    val terminal: Terminal,
    val trace: String,
    val batch: String,
    val hostMessage: String?,
    val datetime: OffsetDateTime,
    val ticket: String,
    val totals: List<Total>,
    val customer: Customer
) {
    data class Total(
        val operationCode: OperationType,
        val amount: String,
        val currency: String
    )

    data class Authorization(
        val code: String? = null,
        val status: Status,
        val retrievalReferenceNumber: String?,
        val displayMessage: String? = null
    ) {
        data class Status(
            val code: String,
            val situation: Situation? = null
        ) {
            data class Situation(
                val id: String,
                val description: String
            )
        }
    }
}
