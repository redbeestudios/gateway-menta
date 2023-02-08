package com.kiwi.api.payments.hexagonal.domain

enum class InputMode {
    MANUAL,
    STRIPE,
    EMV,
    CONTACTLESS,
    ECOMMERCE,
    ONFILE;
}
