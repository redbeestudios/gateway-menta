package com.kiwi.api.payments.domain

enum class PINCapabilities(val code: String) {
    Unknown("0"),
    POSHasPinPad("1"),
    POSDoesntHavePinPad("2"),
    UnusedPinPad("8");
}

enum class POSCondition(val code: String) {
    NormalTransaction("00"),
    CustomerNotPresent("01"),
    UnattendedTerminal("02"),
    CashRegister("04"),
    MailPhoneOrder("08")
}

enum class TerminalType(val code: String) {
    Unspecified("0"),
    DebitCashRegister("4"),
    PhoneDevice("7")
}

enum class Installments(val code: String, val isoField48: String) {
    AHORA_3("13", "703"),
    AHORA_6("16", "706"),
    AHORA_12("7", "712"),
    AHORA_18("8", "718"),
    AHORA_24("25", "724"),
    AHORA_30("31", "730");
}

enum class OperationType {
    PURCHASE, ANNULMENT, REFUND, PURCHASE_REVERSE, ANNULMENT_REVERSE, REFUND_REVERSE
}
