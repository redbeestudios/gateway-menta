package com.menta.bff.devices.login.orchestrate.domain

import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.UserAuth

data class OrchestratedAuth(
    val userAuth: UserAuth,
    val orchestratedEntities: OrchestratedEntities?
)
