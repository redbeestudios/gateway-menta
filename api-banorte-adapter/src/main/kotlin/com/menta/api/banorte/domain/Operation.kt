package com.menta.api.banorte.domain

data class Operation(
    val merchantId: String,
    val commandTransaction: CommandTransaction,
    val amount: String,
    val entryMode: EntryMode,
    val reference: String?,
    val controlNumber: String,
    val card: Card,
    val emvTags: String?,
    val installments: String,
    val aggregator: Aggregator,
    val user: User,
    val terminal: String,
    val affiliationId: String,
    val url: String
) {

    data class Card(
        val pan: String?,
        val expirationDate: String?,
        val cvv: String?,
        val brand: CardBrand,
        val track1: String?,
        val track2: String?,
    ) {
        override fun toString(): String {
            return "Card(brand=$brand, track1=$track1, track2=$track2)"
        }
    }
}
