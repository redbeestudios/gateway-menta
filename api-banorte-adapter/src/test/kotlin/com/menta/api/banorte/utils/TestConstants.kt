package com.menta.api.banorte.utils

import com.menta.api.banorte.domain.CardBrand.AMEX
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TestConstants {

    companion object {
        val HOLDER_NAME = "Melani"
        val CARD_PAN: String = "1234123412341234"
        val CARD_EXPIRATION_DATE = "0325"
        val CONTROL_NUMBER = ""
        val REFERENCE = ""
        val EMV_TAGS = ""
        val MERCHANT_ID = "3020"
        val USER_NAME = ""
        val USER_PASSWORD = ""
        val CARD_BRAND = AMEX
        val AGGREGATOR_ID = ""
        val CHILD_COMMERCE_ID = ""
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
        val INPUT_MODE = "CHIP"
        val CURRENCY = "ARS"
        val TRANSACTION_TRACE = "1456"
        val RRN = "1000"
        val TRANSACTION_BATCH = "888"
        val TRANSACTION_TICKET = "777"
        val TRANSACTION_AUDIT_NUMBER = "123"
        val COMMERCE_CODE = "30001"
        val POINT_OF_SERVICE_CONDITION_CODE = "00"
        val TERMINAL_SERIAL_CODE = "1020"
        val TERMINAL_ID = "1"
        val TERMINAL_SOFTWARE_VERSION = "1"
        val AMOUNT_ADDITIONAL_AMOUNT = "10000000"
        val WORKING_KEY = "123456"
        val INSTALLMENTS = "0"
        val DATE_TIME = OffsetDateTime.of(2025, 2, 23, 13, 13, 23, 999, ZoneOffset.UTC)
    }
}
