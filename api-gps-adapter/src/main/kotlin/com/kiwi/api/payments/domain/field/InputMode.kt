package com.kiwi.api.payments.domain.field

enum class InputMode(val code: String) {
    MANUAL("01"),
    STRIPE("90"),
    EMV("05"),
    CONTACTLESS("07"),
    ECOMMERCE("81"),
    ONFILE("10");
}
