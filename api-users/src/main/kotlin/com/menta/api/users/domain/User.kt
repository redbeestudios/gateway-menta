package com.menta.api.users.domain

import java.util.Date

data class User(
    val attributes: Attributes,
    val enabled: Boolean,
    val status: UserStatus,
    val audit: Audit
) {
    data class Audit(
        val creationDate: Date,
        val updateDate: Date
    )

    data class Attributes(
        val email: Email,
        val merchantId: String?,
        val customerId: String?,
        val type: UserType
    )
}

typealias Email = String
