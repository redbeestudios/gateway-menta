package com.kiwi.api.payments.adapter.jpos.models

enum class FieldPosition(val position: Int) {
    MTI(0),
    PAN(2),
    PROCESS_CODE(3),
    AMOUNT(4),
    TRANSMISSION_DATE_TIME(7),
    AUDIT_NUMBER(11),
    TERMINAL_LOCAL_TIME(12),
    TERMINAL_LOCAL_DATE(13),
    EXPIRATION_DATE(14),
    BATCH_CLOSE_DATE(15),
    INPUT_MODE(22),
    CARD_SEQUENCE_NUMBER(23),
    NETWORK_INTERNATIONAL_IDENTIFIER(24),
    POINT_OF_SERVICE_CONDITION_CODE(25),
    TRACK_2(35),
    RETRIEVAL_REFERENCE_NUMBER(37),
    AUTHORIZATION_CODE(38),
    RESPONSE_CODE(39),
    TERMINAL_IDENTIFICATION(41),
    COMMERCE_NUMBER(42),
    TRACK_1(45),
    ADDITIONAL_DATA_ISO(46),
    ADDITIONAL_DATA_PRIVATE(48),
    CURRENCY(49),
    PIN_DATA(52),
    KSN(53),
    ADDITIONAL_AMOUNTS(54),
    ICC_DATA(55),
    VARIOUS_INDICATORS(59),
    APP_VERSION(60),
    TERMINAL_TYPE(61),
    TICKET_INFORMATION(62),
    ADDITIONAL_INFORMATION(63)
}
