package com.menta.api.users.domain

data class UserWithGroups(
    val user: User,
    val groups: List<Group>,
    val next: String?,
    val limit: Int?
)
