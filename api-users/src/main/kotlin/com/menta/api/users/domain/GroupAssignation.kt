package com.menta.api.users.domain

data class GroupAssignation(
    val user: User,
    val group: Group
) {
    data class Group(
        val name: String
    )
}
