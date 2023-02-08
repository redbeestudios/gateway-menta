package com.menta.bff.devices.login.shared.domain

data class UserCredentials(
    val user: Email,
    val password: String,
    val userType: UserType
) {
    override fun toString(): String {
        return "UserCredentials(user='$user', userType=$userType)"
    }
}

typealias Email = String
