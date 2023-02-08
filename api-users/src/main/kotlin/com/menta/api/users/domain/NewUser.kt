package com.menta.api.users.domain

import java.util.UUID

data class NewUser(
    val type: UserType,
    val attributes: Attributes
) {

    data class Attributes(
        val email: Email,
        val customerId: UUID?,
        val merchantId: UUID?
    )
}
