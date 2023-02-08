package com.menta.api.users.authorities.adapter.`in`.consumer.model

import com.menta.api.users.authorities.domain.UserType
import java.util.Date

data class User(
    val attributes: Attributes,
    val enabled: Boolean,
    val status: String,
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
