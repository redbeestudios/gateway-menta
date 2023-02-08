package com.menta.api.feenicia.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonProperty

data class FeeniciaRefundRequest(
    @JsonProperty("transactionId")
    val transactionId: Long,
    @JsonProperty("transactionDate")
    val transactionDate: Long,
)
