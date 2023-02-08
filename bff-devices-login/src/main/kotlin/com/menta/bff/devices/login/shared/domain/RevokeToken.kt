package com.menta.bff.devices.login.shared.domain

data class RevokeToken(
    val token: String,
    val userType: UserType
) {

    override fun toString(): String {
        return "RevokeToken(userType=$userType)"
    }
}
