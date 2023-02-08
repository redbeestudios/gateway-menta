package com.menta.api.users.shared.other.error.model

import java.time.OffsetDateTime

data class ApiErrorResponse(
    val datetime: OffsetDateTime,
    val errors: List<ApiError>
)

data class ApiError(
    val resource: String,
    val message: String,
    val metadata: Map<String, String>
)
