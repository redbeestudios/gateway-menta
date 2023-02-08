package com.menta.bff.devices.login.shared.domain

data class NewPasswordChallengeSolution(
    val session: String,
    val user: String,
    val userType: UserType,
    val newPassword: String
) {
    override fun toString(): String {
        return "NewPasswordChallengeSolution(user=$user, userType=$userType)"
    }
}
