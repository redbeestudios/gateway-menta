package com.menta.api.transactions.domain

data class Authorization(
    val authorizationCode: String? = null,
    val status: Status,
    val retrievalReferenceNumber: String?,
    val displayMessage: String? = null
) {
    data class Status(
        val code: StatusCode,
        val situation: Situation? = null
    ) {
        data class Situation(
            val id: String,
            val description: String
        )
    }
}
