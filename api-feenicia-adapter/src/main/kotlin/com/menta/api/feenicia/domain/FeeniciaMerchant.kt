package com.menta.api.feenicia.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class FeeniciaMerchant(
    val affiliation: String,
    @JsonProperty("merchant_id")
    val merchantId: String,
    @JsonProperty("user_id")
    val userId: String,
    val email: String,
    val password: String,
    val merchant: String,
    val keys: Keys
) {
    data class Keys(
        @JsonProperty("request_iv")
        val requestIv: String,
        @JsonProperty("request_key")
        val requestKey: String,
        @JsonProperty("request_signature_iv")
        val requestSignatureIv: String,
        @JsonProperty("request_signature_key")
        val requestSignatureKey: String,
        @JsonProperty("response_signature_iv")
        val responseSignatureIv: String,
        @JsonProperty("response_signature_key")
        val responseSignatureKey: String,
        @JsonProperty("response_iv")
        val responseIv: String,
        @JsonProperty("response_key")
        val responseKey: String
    )
}
