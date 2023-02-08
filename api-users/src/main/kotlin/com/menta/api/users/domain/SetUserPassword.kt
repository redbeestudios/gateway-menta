package com.menta.api.users.domain

data class SetUserPassword(
    val type: UserType,
    val email: Email,
    val password: String,
    val permanent: Boolean
)
