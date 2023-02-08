package com.menta.api.users.domain

data class ConfirmForgotPasswordUser(
    val type: UserType,
    val email: Email,
    val password: String,
    val code: String
)
