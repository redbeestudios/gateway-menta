package com.menta.api.feenicia.domain

data class Operation(
    val affiliation: String,
    val amount: Double,
    val cardholderName: String,
    val entryMode: EntryMode,
    val emvRequest: String? = null,
    val geoData: GeoData? = null,
    val items: List<Items>,
    val terminal: String? = null,
    val tip: Double? = 0.0,
    val track2: String? = null,
    val transactionDate: Long,
    // val userId: String,
    val deferralData: DeferralData? = null,
    val contactless: Boolean,
    val transactionId: Long? = null,
    val operationType: OperationType,
    val orderId: String? = null,
    val feeniciaMerchant: FeeniciaMerchant
) {

    data class GeoData(
        val latitude: Float? = null,
        val longitude: Float? = null
    )
    data class DeferralData(
        val planType: String = "03",
        val deferral: String = "00",
        val payments: String,
    )

    data class Items(
        val amount: Double,
        val description: String,
        val id: Int,
        val quantity: Int,
        val unitPrice: String
    )
}
