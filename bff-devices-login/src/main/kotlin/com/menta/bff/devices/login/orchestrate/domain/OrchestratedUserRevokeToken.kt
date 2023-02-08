package com.menta.bff.devices.login.orchestrate.domain

import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.RevokeToken

data class OrchestratedUserRevokeToken(
    val user: Email,
    val revokeToken: RevokeToken,
)
