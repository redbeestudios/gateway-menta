package com.menta.api.feenicia.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonProperty

data class FeeniciaReverseRequest(
    val affiliation: String,
    val amount: Double,
    @JsonProperty("cardholderName")
    val cardholderName: String,
    val track2: String,
    @JsonProperty("transactionDate")
    val transactionDate: Long
)
