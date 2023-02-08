package com.kiwi.api.batchcloses.hexagonal.domain

import java.time.OffsetDateTime

data class Authorization(
    val authorizationCode: String? = null,
    val datetime: OffsetDateTime,
    val status: Status,
    val retrievalReferenceNumber: String,
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
