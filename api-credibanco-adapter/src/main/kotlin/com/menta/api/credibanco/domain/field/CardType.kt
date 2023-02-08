package com.menta.api.credibanco.domain.field

data class CardType(
    val cardIssuerFiid: String,
    val logicalNetwork: String,
    val category: String,
    val saveAccountIndicator: String,
    val interchangeResponseCode: String
)
