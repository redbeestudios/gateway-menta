package com.menta.api.users.adapter.`in`.model

import io.swagger.v3.oas.annotations.media.Schema

data class ConfirmForgotPasswordUserRequest(

    @Schema(description = "code verification", type = "string")
    val code: String,

    @Schema(description = "New password for the user", type = "string")
    val password: String
)
