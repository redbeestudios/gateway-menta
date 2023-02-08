package com.menta.api.feenicia.utils

import com.menta.api.feenicia.domain.CardBrand.AMEX
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TestConstants {

    companion object {
        val HOLDER_NAME = "Test"
        val CARD_PAN: String = "1234123412341234"
        val CARD_EXPIRATION_DATE = "0325"
        val MERCHANT_ID = "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA97"
        val CUSTOMER_ID = "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"
        val CARD_BRAND = AMEX
        val CARD_TYPE = "VISA"
        val CARD_KSN = "658"
        val CARD_ICC_DATA = "456"
        val CARD_PIN = "145"
        val CARD_BANK = "Santander"
        val CARD_CVV = "456"
        val CARD_TRACK1 = "fbdhdhdgh"
        val CARD_TRACK2 = "sghhshdffdfdh"
        val AMOUNT = "1000"
        val DESCRIPTION_BREAKDOWN = "Operation"
        val INPUT_MODE = "EMV"
        val CURRENCY = "ARS"
        val TRANSACTION_TRACE = "1456"
        val RRN = "1000"
        val TRANSACTION_BATCH = "888"
        val TRANSACTION_TICKET = "777"
        val TERMINAL_SERIAL_CODE = "1020"
        val TERMINAL_ID = "1"
        val TERMINAL_SOFTWARE_VERSION = "1"
        val INSTALLMENTS = "03"
        val ORDER_ID = "888888"
        val DATE_TIME = OffsetDateTime.of(2025, 2, 23, 13, 13, 23, 999, ZoneOffset.UTC)
    }
}
