package com.menta.api.login.challenge.adapter.`in`.model

import com.menta.api.login.shared.domain.UserType
import io.swagger.v3.oas.annotations.media.Schema


data class NewPasswordRequiredChallengeRequest(
    @Schema(description = "Session generated when challenge was proposed")
    val session: String,

    @Schema(
        type = "string",
        example = "menta@menta.com",
        description = "User wanting to solve the challenge"
    )
    val user: String,

    @Schema(description = "Type of the user")
    val userType: UserType,

    @Schema(
        type = "string",
        example = "Menta2022!",
        description = "New password for the user"
    )
    val newPassword: String
) {
    override fun toString(): String {
        return "NewPasswordRequiredChallengeRequest(user='$user', userType=$userType)"
    }
}