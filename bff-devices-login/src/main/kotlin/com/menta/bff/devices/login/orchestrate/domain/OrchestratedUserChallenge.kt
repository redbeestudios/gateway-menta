package com.menta.bff.devices.login.orchestrate.domain

import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters

data class OrchestratedUserChallenge(
    val newPasswordChallengeSolution: NewPasswordChallengeSolution,
    val orchestratedEntityParameters: OrchestratedEntityParameters?
)
