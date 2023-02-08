package com.menta.api.merchants.domain.provider

import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Date

@Component
class DateProvider {
    fun provide() = OffsetDateTime.now()

    fun provideFromDate(date: String): Date =
        SimpleDateFormat(FORMAT).parse(date.plus(" 00:00:00"))

    fun provideToDate(date: String): Date =
        SimpleDateFormat(FORMAT).parse(date.plus(" 23:59:59"))

    companion object {
        private const val FORMAT = "yyyy-MM-dd H:m:s"
    }
}
