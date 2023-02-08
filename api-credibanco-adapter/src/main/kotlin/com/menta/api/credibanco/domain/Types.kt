package com.menta.api.credibanco.domain

enum class PINCapabilities(val code: String) {
    Unknown("0"),
    POSHasPinPad("1"),
    POSDoesntHavePinPad("2"),
    UnusedPinPad("8");
}

enum class OperationType {
    PURCHASE, ANNULMENT, REFUND, PAYMENT_REVERSE, ANNULMENT_REVERSE
}

enum class CardType {
    DEBIT, CREDIT
}
