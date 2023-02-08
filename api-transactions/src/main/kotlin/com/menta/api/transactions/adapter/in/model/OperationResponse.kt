package com.menta.api.transactions.adapter.`in`.model

import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode
import java.time.OffsetDateTime
import java.util.UUID

data class OperationResponse(
    val id: UUID,
    val ticketId: Int?,
    val datetime: OffsetDateTime,
    val type: OperationType,
    val status: StatusCode,
    val amount: String,
    val acquirerId: String?
)
