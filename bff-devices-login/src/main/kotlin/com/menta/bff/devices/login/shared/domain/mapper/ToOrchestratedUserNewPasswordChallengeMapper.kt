package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedNewPasswordChallengeRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserChallenge
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToOrchestratedUserNewPasswordChallengeMapper(
    private val toOrchestratedEntityParametersMapper: ToOrchestratedEntityParametersMapper
) {

    fun mapFrom(request: OrchestratedNewPasswordChallengeRequest): OrchestratedUserChallenge =
        with(request) {
            OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = session,
                    user = user,
                    userType = userType,
                    newPassword = newPassword
                ),
                orchestratedEntityParameters = orchestratedEntities?.let {
                    toOrchestratedEntityParametersMapper.mapFrom(it)
                }
            )
        }.log { info("orchestrated user challenge mapped: {}", it) }

    companion object : CompanionLogger()
}
