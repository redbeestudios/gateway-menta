package com.menta.api.transactions.domain

data class Card(
    val type: CardType?,
    val mask: String?,
    val brand: String?,
    val bank: String?,
    val holder: Holder?
) {
    data class Holder(
        val name: String?,
        val document: String?
    )
}
