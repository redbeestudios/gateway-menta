package com.menta.bff.devices.login.login.challenge.adapter.out.model

import com.menta.bff.devices.login.shared.domain.UserType

data class NewPasswordChallengeClientRequest(
    val session: String,
    val user: String,
    val userType: UserType,
    val newPassword: String
) {
    override fun toString(): String {
        return "NewPasswordChallengeRequest(user='$user', userType=$userType)"
    }
}
