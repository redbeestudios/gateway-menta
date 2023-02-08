package com.menta.libs.security.requesterUser.model

import java.util.UUID

data class RequesterUser(
    val type: UserType,
    val name: String,
    val attributes: Attributes
) {
    enum class UserType {
        MERCHANT, CUSTOMER, SUPPORT;
    }

    data class Attributes(
        val customerId: UUID?,
        val merchantId: UUID?,
        val email: String
    )
}
