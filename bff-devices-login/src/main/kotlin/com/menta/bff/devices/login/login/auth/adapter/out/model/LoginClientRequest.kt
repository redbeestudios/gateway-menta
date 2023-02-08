package com.menta.bff.devices.login.login.auth.adapter.out.model

import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserType

data class LoginClientRequest(
    val user: Email,
    val password: String,
    val userType: UserType
) {
    override fun toString(): String {
        return "LoginClientRequest(user='$user', userType=$userType)"
    }
}
