package com.menta.bff.devices.login.login.refresh.adapter.out.model

import com.menta.bff.devices.login.shared.domain.UserType

data class RefreshClientRequest(
    val refreshToken: String,
    val userType: UserType
)
