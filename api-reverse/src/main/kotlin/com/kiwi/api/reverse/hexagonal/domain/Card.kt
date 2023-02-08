package com.kiwi.api.reverse.hexagonal.domain

data class Card(
    val holder: Holder,
    val pan: String?,
    val expirationDate: String?,
    val cvv: String?,
    val track1: String?,
    val track2: String?,
    val iccData: String?,
    val cardSequenceNumber: String?,
    val bank: String,
    val type: String,
    val brand: String
) {
    data class Holder(
        val name: String,
        val identification: Identification
    ) {
        data class Identification(
            val number: String,
            val type: String
        )
    }
}

