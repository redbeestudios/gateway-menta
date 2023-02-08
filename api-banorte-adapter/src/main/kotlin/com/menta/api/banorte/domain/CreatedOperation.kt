package com.menta.api.banorte.domain

import java.time.OffsetDateTime

data class CreatedOperation(
    val controlNumber: String, // id o trace?
    val referenceNumber: String,
    val paywResult: PawsResult,
    val authResult: AuthResult,
    val paywCode: String?,
    val authorizationCode: String,
    val text: String,
    val affiliationId: String,
    val emvData: String,
    val custReqDate: OffsetDateTime,
    val card: Card
) {
    data class Card(
        val referredPan: String?,
        val bank: String,
        val type: String,
        val brand: CardBrand,
    )

    enum class PawsResult(val description: String) {
        A("APPROVED"),
        T("FAILED"),
        D("FAILED"),
        R("FAILED")
    }
}
