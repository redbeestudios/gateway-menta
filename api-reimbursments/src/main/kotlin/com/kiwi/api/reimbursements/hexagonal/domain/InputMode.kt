package com.kiwi.api.reimbursements.hexagonal.domain

enum class InputMode {
    MANUAL,
    STRIPE,
    EMV,
    CONTACTLESS,
    ECOMMERCE,
    ONFILE;
}
