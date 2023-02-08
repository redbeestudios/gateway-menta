package com.menta.api.users.domain

data class ListGroupsByUserQueryResult(
    val groups: List<Group>,
    val next: String?
)
