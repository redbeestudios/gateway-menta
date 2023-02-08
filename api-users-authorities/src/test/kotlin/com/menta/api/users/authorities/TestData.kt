package com.menta.api.users.authorities

import com.menta.api.users.authorities.adapter.`in`.consumer.model.User
import com.menta.api.users.authorities.domain.UserAssignAuthority
import com.menta.api.users.authorities.domain.UserAuthority
import com.menta.api.users.authorities.domain.UserType.MERCHANT
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.UUID

val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))

val email = "user@menta.global"
val authority = "Payment::Create"
val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val merchantId = UUID.fromString("669ed2f9-4c04-4c85-8b48-1a8fd67de963")
val createDate = Date.from(Instant.ofEpochSecond(1234))
val updateDate = Date.from(Instant.ofEpochSecond(5667))

val anUserAuthority = UserAuthority(
    type = MERCHANT,
    authorities = mutableListOf(authority)
)

val anUserAssignAuthority = UserAssignAuthority(
    user = email,
    authority = authority,
    type = MERCHANT
)

val anUser = User(
    attributes = User.Attributes(
        email = email,
        merchantId = merchantId.toString(),
        customerId = customerId.toString(),
        type = MERCHANT
    ),
    enabled = true,
    status = "CONFIRMED",
    audit = User.Audit(
        creationDate = createDate,
        updateDate = updateDate
    )
)
