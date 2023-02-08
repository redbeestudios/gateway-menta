package com.menta.api.transactions

import java.time.OffsetDateTime
import java.time.ZoneOffset

class TestConstants {

    companion object {
        // OPERATION
        const val OPERATION_ID = "2fce7d49-f3e2-4b1f-a7bb-7f16d3ea64a2"
        const val TICKET_ID = 111111111
        const val OPERATION_TYPE = "PAYMENT"
        const val PAYMENT_ID = "2fce7d49-f3e2-4b1f-a7bb-7f16d3ea64a2"

        // TRANSACTION
        const val TRANSACTION_ID = "3b7b8f45-cc8c-4407-af7b-e759f8ed3573"
        const val TRANSACTION_TYPE = "ACQUIRER"
        const val MERCHANT_ID = "db3ae307-4a5c-4cbb-b48c-80bf1a3fde1d"
        const val CUSTOMER_ID = "db3ae307-4a5c-4cbb-b48c-80bf1a3fde1d"
        const val TERMINAL_ID = "72171704-7806-4347-b08b-bc2d2d96e68e"
        const val AMOUNT = "200000"
        const val REFUNDED_AMOUNT = "200000"
        const val CURRENCY = "ARS"
        const val SERIAL_CODE = "50010531"

        // INSTALLMENTS
        const val INSTALLMENTS_NUMBER = "2"
        const val INSTALLMENTS_PLAN = "plan"

        // HOLDER
        const val HOLDER_NAME = "John Doe"
        const val HOLDER_DOCUMENT = "99999999"

        // CARD
        const val CARD_MASK = "XXXXXXXXXXXX0037"
        const val CARD_BRAND = "VISA"
        const val CARD_BANK = "SANTANDER"
        const val CARD_TYPE = "CREDIT"
        const val CARD_TYPE_DEBIT = "DEBIT"

        // AUTHORIZATION
        const val AUTHORIZATION_CODE = "123456789"
        const val AUTHORIZATION_STATUS_CODE = "aprobado"
        const val AUTHORIZATION_STATUS_SITUATION_ID = "id"
        const val AUTHORIZATION_STATUS_SITUATION_DESCRIPTION = "description"
        const val AUTHORIZATION_RRN = "123456789"
        const val AUTHORIZATION_MESSAGE = "a message"

        fun aDatetime() = OffsetDateTime.of(2022, 12, 31, 23, 23, 23, 0, ZoneOffset.UTC)
    }
}
