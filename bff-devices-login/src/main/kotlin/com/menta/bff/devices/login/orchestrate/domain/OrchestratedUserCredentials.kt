package com.menta.bff.devices.login.orchestrate.domain

import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.domain.UserCredentials

data class OrchestratedUserCredentials(
    val userCredentials: UserCredentials,
    val orchestratedEntityParameters: OrchestratedEntityParameters?
)
