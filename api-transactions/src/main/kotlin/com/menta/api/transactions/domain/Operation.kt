package com.menta.api.transactions.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Operation(
    val id: UUID,
    val ticketId: Int?,
    val datetime: OffsetDateTime,
    val type: OperationType,
    val status: StatusCode,
    val amount: String,
    val acquirerId: String?,
)
