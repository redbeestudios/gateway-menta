package com.menta.bff.devices.login.entities.user

import com.menta.bff.devices.login.entities.user.domain.User
import com.menta.bff.devices.login.shared.domain.UserType
import com.menta.bff.devices.login.shared.domain.UserType.CUSTOMER
import com.menta.bff.devices.login.shared.domain.UserType.SUPPORT
import java.time.Instant
import java.util.Date
import java.util.UUID

val customerId: UUID = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val merchantId: UUID = UUID.fromString("669ed2f9-4c04-4c85-8b48-1a8fd67de963")

fun aMerchantUser() =
    User(
        status = User.UserStatus.CONFIRMED,
        attributes = User.Attributes(
            email = "email@menta.global",
            customerId = customerId,
            merchantId = merchantId,
            type = UserType.MERCHANT
        ),
        audit = User.Audit(
            creationDate = Date.from(Instant.ofEpochSecond(12345)),
            updateDate = Date.from(Instant.ofEpochSecond(65678))
        )
    )

fun aCustomerUser() =
    aMerchantUser().let {
        it.copy(attributes = it.attributes.copy(type = CUSTOMER, merchantId = null))
    }

fun aSupportUser() =
    aMerchantUser().let {
        it.copy(attributes = it.attributes.copy(type = SUPPORT, merchantId = null, customerId = null))
    }
