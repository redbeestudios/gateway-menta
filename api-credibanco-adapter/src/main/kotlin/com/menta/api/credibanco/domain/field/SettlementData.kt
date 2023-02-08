package com.menta.api.credibanco.domain.field

data class SettlementData(
    val service: String,
    val originator: String,
    val destination: String,
    val draftCaptureFlag: String,
    val settlementFlag: String
)
