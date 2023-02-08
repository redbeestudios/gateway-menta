package com.menta.api.customers.shared.error.model

import java.time.OffsetDateTime

data class ApiErrorResponse(
    val datetime: OffsetDateTime,
    val errors: List<ApiError>

) {
    data class ApiError(
        val resource: String,
        val message: String,
        val metadata: Map<String, String>
    )
}
