package com.menta.api.login.login.adapter.`in`.model

import com.menta.api.login.shared.domain.UserType
import com.menta.api.login.shared.domain.UserType.MERCHANT
import io.swagger.v3.oas.annotations.media.Schema

data class LoginRequest(
    @Schema(type = "string", example = "mail@menta.global")
    val user: String,
    @Schema(type = "string", example = "Menta2022")
    val password: String,
    val userType: UserType = MERCHANT
) {
    override fun toString(): String {
        return "LoginRequest(user='$user', userType=$userType)"
    }
}
