package com.menta.api.users.domain

data class ListGroupByUserQuery(
    val user: User,
    val search: Search
) {
    data class Search(
        val limit: Int?,
        val next: String?
    )
}
