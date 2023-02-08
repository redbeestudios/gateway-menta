package com.menta.api.login.refresh.domain

import com.menta.api.login.shared.domain.UserType


data class Refresh(
    val token: String,
    val userType: UserType
) {

    override fun toString(): String {
        return "Refresh(userType=$userType)"
    }
}