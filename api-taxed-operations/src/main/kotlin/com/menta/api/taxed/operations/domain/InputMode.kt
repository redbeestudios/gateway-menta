package com.menta.api.taxed.operations.domain

enum class InputMode {
    MANUAL,
    STRIPE,
    EMV,
    CONTACTLESS,
    ECOMMERCE,
    ONFILE;
}
