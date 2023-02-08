package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.menta.bff.devices.login.login.auth.application.service.LoginApplicationService
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedLoginInPort
import com.menta.bff.devices.login.orchestrate.application.service.OrchestrateEntitiesService
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserCredentials
import com.menta.bff.devices.login.orchestrate.domain.mapper.ToOrchestratedAuthMapper
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.domain.UserCredentials
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class OrchestratedLoginUseCase(
    private val loginApplicationService: LoginApplicationService,
    private val orchestrateEntities: OrchestrateEntitiesService,
    private val toOrchestratedAuthMapper: ToOrchestratedAuthMapper
) : OrchestratedLoginInPort {

    override fun login(orchestratedUserCredentials: OrchestratedUserCredentials): Either<ApplicationError, OrchestratedAuth> =
        with(orchestratedUserCredentials) {
            userCredentials.doLogin().flatMap { userAuth ->
                orchestrateEntities(userAuth).map { entities ->
                    buildOrchestratedAuth(userAuth, entities)
                }
            }
        }

    private fun UserCredentials.doLogin() =
        loginApplicationService.login(this)
            .logRight { info("user logged in: {}", it) }

    private fun OrchestratedUserCredentials.orchestrateEntities(userAuth: UserAuth) =
        orchestrateEntities.orchestrate(
            userCredentials.user,
            userCredentials.userType,
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
