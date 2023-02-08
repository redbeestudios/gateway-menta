package com.menta.api.credibanco.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class CredibancoMerchant(
    @JsonProperty("merchant_id")
    val merchantId: UUID,
    @JsonProperty("commerce_code")
    val commerceCode: String,
    val category: String
)
