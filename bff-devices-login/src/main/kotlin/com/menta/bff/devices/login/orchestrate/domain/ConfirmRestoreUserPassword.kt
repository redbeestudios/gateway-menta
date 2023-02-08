package com.menta.bff.devices.login.orchestrate.domain

import com.menta.bff.devices.login.shared.domain.UserType

data class ConfirmRestoreUserPassword(
    val code: String,
    val user: String,
    val userType: UserType,
    val newPassword: String
) {
    override fun toString(): String {
        return "ConfirmRestoreUserPassword(user=$user, userType=$userType)"
    }
}
