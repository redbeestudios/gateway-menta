package com.kiwi.api.payments.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class AcquirerMerchant(
    @JsonProperty("merchant_id")
    val merchantId: String,
    @JsonProperty("acquirer")
    val acquirerId: String,
    val code: String
)
