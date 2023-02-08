package com.menta.api.credibanco.domain.field

enum class InputMode(val code: String) {
    STRIPE("02"),
    EMV("05"),
    CONTACTLESS("07")
    // falta 80 fallBack de credibanco
}
