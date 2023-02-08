package com.menta.bff.devices.login.orchestrate.domain

import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.domain.Refresh

data class OrchestratedUserRefresh(
    val user: Email,
    val refresh: Refresh,
    val orchestratedEntityParameters: OrchestratedEntityParameters?
)
