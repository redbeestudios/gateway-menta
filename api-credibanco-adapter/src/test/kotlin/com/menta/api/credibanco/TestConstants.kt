package com.menta.api.credibanco

import java.time.OffsetDateTime
import java.time.ZoneOffset

class TestConstants {

    companion object {
        const val CARD_PAN = "5413330089020011"
        const val TOTAL_AMOUNT = "200000"
        const val TRANSACTION_RRN = "123456789012"
        const val AUDIT_NUMBER = "12345678"
        const val AUTHORIZATION_CODE = "00"
        const val MERCHANT_ID = "5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"
        const val TERMINAL_ID = "5b1fc6c0-9bba-4dfa-b128-786e52f6aa96"
        const val TERMINAL_MESSAGE = "a message"
        const val TERMINAL_USE_CODE = "DISPLAY_TEXT"
        const val WORKING_KEY = "123456"
        const val CARD_TYPE = "5110TES200000000000"
        const val AGGREGATOR_CODE = "555"
        const val RECEIVING_INSTITUTION_IDENTIFICATION_CODE = "10000005110"
        const val SETTLEMENT_DATA_RESPONSE = "C5B24 B24 1 "
        const val OPERATION_RESPONSE_ID = "3b7b8f45-cc8c-4407-af7b-e759f8ed3573"

        val DATETIME = OffsetDateTime.of(2025, 2, 23, 13, 13, 23, 999, ZoneOffset.UTC)
    }
}
