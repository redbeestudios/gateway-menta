package com.menta.api.transactions.domain

enum class InputMode {
    MANUAL,
    STRIPE,
    EMV,
    CONTACTLESS,
    ECOMMERCE,
    ONFILE;
}
