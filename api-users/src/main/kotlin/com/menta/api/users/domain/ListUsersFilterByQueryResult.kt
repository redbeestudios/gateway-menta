package com.menta.api.users.domain

data class ListUsersFilterByQueryResult(
    val users: List<User>,
    val next: String?
)
