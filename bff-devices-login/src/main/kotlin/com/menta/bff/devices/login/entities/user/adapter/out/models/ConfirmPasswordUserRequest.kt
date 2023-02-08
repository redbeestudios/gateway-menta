package com.menta.bff.devices.login.entities.user.adapter.out.models

data class ConfirmPasswordUserRequest(
    val code: String,
    val password: String
)
