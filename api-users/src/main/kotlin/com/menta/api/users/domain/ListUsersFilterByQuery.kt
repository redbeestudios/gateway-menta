package com.menta.api.users.domain

import java.util.UUID

data class ListUsersFilterByQuery(
    val type: UserType,
    val email: Email?,
    val customerId: UUID?,
    val merchantId: UUID?,
    val search: Search
) {
    data class Search(
        val limit: Int?,
        val next: String?
    )
}
