package com.kiwi.api.payments.domain.field

enum class TerminalFeature(val code: String) {
    MANUAL("1"),
    STRIPE("2"),
    CHIP("5"),
    CONTACTLESS("7")
}
