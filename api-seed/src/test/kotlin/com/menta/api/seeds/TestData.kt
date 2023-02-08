package com.menta.api.seeds

import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset

val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))
