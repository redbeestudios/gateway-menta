package com.menta.api.feenicia.extention

import java.time.OffsetDateTime
import java.time.Year
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("YYYYMMddhhmmss")
fun String.toOffsetDateTime(): OffsetDateTime = try {
    OffsetDateTime.parse(Year.now().toString().plus(this), formatter)
} catch (e: Exception) {
    OffsetDateTime.now()
}
