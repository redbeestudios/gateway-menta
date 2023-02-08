package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.menta.bff.devices.login.login.challenge.application.service.NewPasswordChallengeApplicationService
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedNewPasswordChallengeInPort
import com.menta.bff.devices.login.orchestrate.application.service.OrchestrateEntitiesService
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserChallenge
import com.menta.bff.devices.login.orchestrate.domain.mapper.ToOrchestratedAuthMapper
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class OrchestratedNewPasswordChallengeUseCase(
    private val newPasswordChallengeApplicationService: NewPasswordChallengeApplicationService,
    private val orchestrateEntities: OrchestrateEntitiesService,
    private val toOrchestratedAuthMapper: ToOrchestratedAuthMapper
) : OrchestratedNewPasswordChallengeInPort {

    override fun solve(orchestratedUserChallenge: OrchestratedUserChallenge): Either<ApplicationError, OrchestratedAuth> =
        with(orchestratedUserChallenge) {
            newPasswordChallengeSolution.doSolve().flatMap { userAuth ->
                orchestrateEntities(userAuth).map { entities ->
                    buildOrchestratedAuth(userAuth, entities)
                }
            }
        }

    private fun NewPasswordChallengeSolution.doSolve() =
        newPasswordChallengeApplicationService.solve(this)
            .logRight { info("user challenge in: {}", it) }

    private fun OrchestratedUserChallenge.orchestrateEntities(userAuth: UserAuth) =
        orchestrateEntities.orchestrate(
            newPasswordChallengeSolution.user,
            newPasswordChallengeSolution.userType,
            userAuth,
            orchestratedEntityParameters
        ).logRight { info("orchestrated entities: {}", it) }

    private fun buildOrchestratedAuth(
        userAuth: UserAuth,
        orchestrateEntities: OrchestratedEntities?
    ) =
        toOrchestratedAuthMapper.mapFrom(userAuth, orchestrateEntities)
            .log { info("orchestrated auth mapped: {}", it) }

    companion object : CompanionLogger()
}
