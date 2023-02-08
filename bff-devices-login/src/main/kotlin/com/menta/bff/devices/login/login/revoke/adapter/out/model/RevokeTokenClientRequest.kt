package com.menta.bff.devices.login.login.revoke.adapter.out.model

import com.menta.bff.devices.login.shared.domain.UserType

data class RevokeTokenClientRequest(
    val refreshToken: String,
    val userType: UserType
)
