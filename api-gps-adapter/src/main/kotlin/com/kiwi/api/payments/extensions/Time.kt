package com.kiwi.api.payments.extensions

import java.time.OffsetDateTime
import java.time.Year
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("YYYYMMddhhmmss")
private val acquirerFormatter = DateTimeFormatter.ofPattern("MMddhhmmss")

fun OffsetDateTime.toAcquirerFormat(): String =
    format(acquirerFormatter)

fun OffsetDateTime.toDateInAcquirerFormat(): String =
    format(DateTimeFormatter.ofPattern("MMdd"))

fun OffsetDateTime.toTimeInAcquirerFormat(): String =
    format(DateTimeFormatter.ofPattern("hhmmss"))

fun String.toOffsetDateTime(): OffsetDateTime = try {
    // TODO: Ver si realmente se necesita
    OffsetDateTime.parse(Year.now().toString().plus(this), formatter)
} catch (e: Exception) {
    OffsetDateTime.now()
}
