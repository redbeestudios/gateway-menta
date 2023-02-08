package com.menta.bff.devices.login.shared.domain

data class Refresh(
    val token: String,
    val userType: UserType
) {

    override fun toString(): String {
        return "Refresh(userType=$userType)"
    }
}
