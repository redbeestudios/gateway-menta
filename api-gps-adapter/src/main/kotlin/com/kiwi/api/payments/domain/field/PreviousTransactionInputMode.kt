package com.kiwi.api.payments.domain.field

enum class PreviousTransactionInputMode(val code: String) {
    NO_CHIP(" "),
    CHIP("1"),
    FALLBACK("2");
}
