package com.kiwi.api.payments.application

import com.kiwi.api.payments.domain.State.CORDOBA
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TestConstants {

    companion object {
        const val IDENTIFICACION_NAME = "JOSE ANTONIO PEREZ"
        const val IDENTIFICATION_NUMBER = "99999999"
        const val IDENTIFICATION_TYPE = "DNI"
        const val CARD_PAN = "1234123412341234"
        const val CARD_MASKED_PAN = "XXXXXXXXXXXX1234"
        const val CARD_EXPIRATION_DATE = "2212"
        const val CARD_SEQUENCE_NUMBER = "12341234"
        const val CARD_KSN = "1111111111000002"
        const val CARD_ICC_DATA = "9F3303E0F8C8950500000000009F3704CF1596BF9F1E0835303031303533319F100706010A03A028009F2608F8580449D75C66ED9F360200F1820220009C01009A032202229F02060000000050009F03060000000000009F2701805F2A0206829F1A0208409F34031F00008407A00000000310109F6E0420700000"
        const val CARD_PIN = "8661C836ACBB220A"
        const val CARD_CVV = "456"
        const val CARD_BANK = "Santander"
        const val CARD_TYPE = "DEBIT"
        const val CARD_BRAND = "VISA"
        const val CARD_TRACK1 = "KJAHFKDAH"
        const val CARD_TRACK2 = "4558950023816600D25122261000062700000F"
        const val CARD_NII = "FJAHDFKAJHFKAJH"
        const val CARD_RRN = "123456789012"
        const val PREVIOUS_TRANSACTION_INPUT_MODE = "CHIP"
        const val INPUT_MODE = "MANUAL"
        const val TOTAL_AMOUNT = "2300000000"
        const val TIP_AMOUNT = "300000000"
        const val OPERATION_AMOUNT = "2000000000"
        const val CURRENCY_AMOUNT = "ARS"
        const val TRANSACTION_BATCH = "888"
        const val TRANSACTION_TICKET = "777"
        const val TRANSACTION_TRACE = "123"
        const val TRANSACTION_RRN = "123456789012"
        const val TRANSACTION_INSTALLMENTS = "10"
        const val COMMERCE_CODE = "123"
        const val AUDIT_NUMBER = "12345678"
        const val AUTHORIZATION_CODE = "123456789012"
        const val MERCHANT_ID = "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"
        const val TERMINAL_ID = "5b1fc6c0-9bba-4dfa-b128-786e52f6aa96"
        const val TERMINAL_SERIAL_CODE = "03000021"
        const val TERMINAL_SOFTWARE_VERSION = "10"
        const val TERMINAL_HARDWARE = "abc123"
        const val TERMINAL_HANDBOOK_VERSION = "200"
        const val TERMINAL_MESSAGE = "a message"
        const val TERMINAL_USE_CODE = "DISPLAY_TEXT"
        const val WORKING_KEY = "123456"
        const val AGGREGATOR_NAME = "fantasyName1"
        const val AGGREGATOR_CODE = "555"
        const val AGGREGATOR_ADDRESS = "Street1 123 CABA, Argentina"
        const val AGGREGATOR_CHILD_COMMERCE_NAME = "Menta"
        const val AGGREGATOR_CHILD_COMMERCE_CODE = "123"
        val AGGREGATOR_CHILD_COMMERCE_STATE = CORDOBA
        const val AGGREGATOR_CHILD_COMMERCE_CITY = "CABA"
        const val AGGREGATOR_CHILD_COMMERCE_ZIP = "123"
        const val AGGREGATOR_CHILD_COMMERCE_CATEGORY_CODE = "7372"

        val DATETIME = OffsetDateTime.of(2025, 2, 23, 13, 13, 23, 999, ZoneOffset.UTC)
    }
}
