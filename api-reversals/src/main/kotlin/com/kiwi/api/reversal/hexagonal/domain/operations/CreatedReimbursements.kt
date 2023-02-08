package com.kiwi.api.reversal.hexagonal.domain.operations

data class CreatedAnnulment(
    val id: String,
    val authorization: Authorization,
    val data: Annulment
)

data class CreatedRefund(
    val id: String,
    val authorization: Authorization,
    val data: Refund
)
