package com.kiwi.api.payments.domain.field

data class Installments(
    val financing: Financing,
    val quantity: String
) {
    enum class Financing {
        AHORA,
        BANK
    }
}
