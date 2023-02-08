package com.menta.bff.devices.login.orchestrate.domain

import com.menta.bff.devices.login.shared.domain.UserType

data class OrchestratedRestoreUserPassword(
    val user: String,
    val userType: UserType
)
