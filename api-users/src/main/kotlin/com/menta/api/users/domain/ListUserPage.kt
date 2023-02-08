package com.menta.api.users.domain

data class ListUserPage(
    val users: List<User>,
    val next: String?,
    val limit: Int?
)
