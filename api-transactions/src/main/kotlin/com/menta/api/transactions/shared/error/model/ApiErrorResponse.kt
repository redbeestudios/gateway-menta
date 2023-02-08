package com.menta.api.transactions.shared.error.model

import java.time.OffsetDateTime

data class ApiErrorResponse(
    val datetime: OffsetDateTime,
    val errors: List<ApiError>

) {
    data class ApiError(
        val code: String,
        val resource: String,
        val message: String,
        val metadata: Map<String, String>
    )
}
