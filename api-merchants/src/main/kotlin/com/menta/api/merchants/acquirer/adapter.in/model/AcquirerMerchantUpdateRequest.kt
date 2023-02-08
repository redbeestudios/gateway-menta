package com.menta.api.merchants.acquirer.adapter.`in`.model

import io.swagger.v3.oas.annotations.media.Schema

data class AcquirerMerchantUpdateRequest(
    @Schema(type = "string", example = "GPS", description = "an acquirer id")
    val acquirerId: String,
    @Schema(type = "string", example = "1234", description = "a code")
    val code: String
)
