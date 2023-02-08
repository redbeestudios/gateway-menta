package com.menta.api.feenicia.domain

data class CreatedOperation(
    val affiliation: String? = null,
    val authnum: String? = null,
    val responseCode: String,
    val transactionId: String? = null,
    val merchant: Merchant? = null,
    val amount: Double? = null,
    val tip: Double? = null,
    val currency: Currency? = null,
    val issuerBank: IssuerBank? = null,
    val acquirerBank: AcquirerBank? = null,
    val approved: Boolean? = null,
    val receiptId: String? = null,
    val folio: String? = null,
    val emvResponse: String? = null,
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
            val last4Digits: String,
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
