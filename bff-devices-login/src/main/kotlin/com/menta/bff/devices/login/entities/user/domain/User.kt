package com.menta.bff.devices.login.entities.user.domain

import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserType
import java.util.Date
import java.util.UUID

data class User(
    val attributes: Attributes,
    val status: UserStatus,
    val audit: Audit
) {

    enum class UserStatus {
        UNCONFIRMED, CONFIRMED, ARCHIVED, UNKNOWN, RESET_REQUIRED, FORCE_CHANGE_PASSWORD, COMPROMISED;
    }

    data class Audit(
        val creationDate: Date,
        val updateDate: Date
    )

    data class Attributes(
        val email: Email,
        val merchantId: UUID?,
        val customerId: UUID?,
        val type: UserType
    )
}
