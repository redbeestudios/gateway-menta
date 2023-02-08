package com.menta.api.feenicia.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class FeeniciaRequest(
    val affiliation: String,
    val amount: Double,
    @JsonProperty("cardholderName")
    val cardholderName: String,
    @JsonProperty("emvRequest")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val emvRequest: String? = null,
    @JsonProperty("geoData")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    val geoData: GeoData? = null,
    val items: List<Items>,
    @JsonInclude(JsonInclude.Include.ALWAYS)
    val terminal: String? = null,
    @JsonInclude(JsonInclude.Include.ALWAYS)
    val tip: Double? = 0.0,
    val track2: String,
    @JsonProperty("transactionDate")
    val transactionDate: Long,
    @JsonProperty("userId")
    val userId: String,
    @JsonProperty("deferralData")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    val deferralData: DeferralData? = null,
    val contactless: Boolean
) {

    data class GeoData(
        val latitude: Float? = null,
        val longitude: Float? = null
    )
    data class DeferralData(
        @JsonProperty("planType")
        val planType: String,
        val deferral: String = "00",
        val payments: String,
    )

    data class Items(
        val amount: Double = 0.0,
        val description: String = "",
        val id: Int = 0,
        val quantity: Int = 1,
        @JsonProperty("unitPrice")
        val unitPrice: String = "0.0"
    )
}
