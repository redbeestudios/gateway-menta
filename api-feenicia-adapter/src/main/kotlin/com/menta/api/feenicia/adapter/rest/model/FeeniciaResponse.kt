package com.menta.api.feenicia.adapter.rest.model

import com.fasterxml.jackson.annotation.JsonProperty

data class FeeniciaResponse(
    val affiliation: String? = null,
    val authnum: String? = null,
    @JsonProperty("responseCode")
    val responseCode: String,
    @JsonProperty("transactionId")
    val transactionId: String? = null,
    val merchant: Merchant? = null,
    val amount: Double? = null,
    val tip: Double? = null,
    val currency: Currency? = null,
    @JsonProperty("issuerBank")
    val issuerBank: IssuerBank? = null,
    @JsonProperty("acquirerBank")
    val acquirerBank: AcquirerBank? = null,
    val approved: Boolean? = null,
    @JsonProperty("receiptId")
    val receiptId: String? = null,
    val folio: String? = null,
    @JsonProperty("emvResponse")
    val emvResponse: String? = null,
    @JsonProperty("orderId")
    val orderId: String? = null
) {
    data class Merchant(
        val name: String,
        val address: String,
        val id: String,
        val card: Card? = null,
    ) {
        data class Card(
            val product: String,
            val brand: String,
            @JsonProperty("last4Digits")
            val last4Digits: String,
            @JsonProperty("first6Digits")
            val first6Digits: String,
        )
    }
    data class Currency(
        val id: Int,
        val description: String
    )
    data class IssuerBank(
        val name: String? = null,
    )
    data class AcquirerBank(
        val name: String,
    )
}
