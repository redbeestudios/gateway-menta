package com.menta.bff.devices.login.orchestrate.adapter.`in`.model

import com.menta.bff.devices.login.shared.domain.UserType
import io.swagger.v3.oas.annotations.media.Schema

data class ConfirmRestoreUserPasswordRequest(

    @Schema(
        example = "menta@menta.com",
        description = "User wanting to solve the challenge"
    )
    val user: String,

    @Schema(description = "Type of the user")
    val userType: UserType,

    @Schema(description = "code verification received into a mail", example = "789563")
    val code: String,

    @Schema(description = "New password for the user", example = "Menta2022!")
    val newPassword: String
)
