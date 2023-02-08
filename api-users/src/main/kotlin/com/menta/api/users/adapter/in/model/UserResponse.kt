package com.menta.api.users.adapter.`in`.model

import com.menta.api.users.domain.Email
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserType
import java.util.Date

data class UserResponse(
    val attributes: Attributes,
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
