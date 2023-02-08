package com.menta.api.users.authorities.domain

data class UserAssignAuthority(
    val user: String,
    val type: UserType,
    val authority: String
)
