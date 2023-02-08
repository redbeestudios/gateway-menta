package com.menta.api.users.authorities.domain

data class UserAuthority(
    val type: UserType,
    val authorities: List<String>
)
