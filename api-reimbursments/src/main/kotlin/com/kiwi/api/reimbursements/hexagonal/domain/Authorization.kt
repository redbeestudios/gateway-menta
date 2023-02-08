package com.kiwi.api.reimbursements.hexagonal.domain

data class Authorization(
    val authorizationCode: String?,
    val status: Status,
    val retrievalReferenceNumber: String?,
    val displayMessage: DisplayMessage?
) {
    data class DisplayMessage(
        val useCode: String,
        val message: String,
    )

    data class Status(
        val code: String,
        val situation: Situation?
    ) {
        data class Situation(
            val id: String,
            val description: String
        )
    }
}
