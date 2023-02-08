package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.menta.bff.devices.login.login.refresh.application.service.RefreshApplicationService
import com.menta.bff.devices.login.orchestrate.application.port.`in`.OrchestratedRefreshInPort
import com.menta.bff.devices.login.orchestrate.application.service.OrchestrateEntitiesService
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRefresh
import com.menta.bff.devices.login.orchestrate.domain.mapper.ToOrchestratedAuthMapper
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.UserAuth
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import com.menta.bff.devices.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class OrchestratedRefreshUseCase(
    private val refreshApplicationService: RefreshApplicationService,
    private val orchestrateEntities: OrchestrateEntitiesService,
    private val toOrchestratedAuthMapper: ToOrchestratedAuthMapper
) : OrchestratedRefreshInPort {

    override fun refresh(orchestratedUserRefresh: OrchestratedUserRefresh): Either<ApplicationError, OrchestratedAuth> =
        with(orchestratedUserRefresh) {
            refresh.doRefresh().flatMap { userAuth ->
                orchestrateEntities(userAuth).map { entities ->
                    buildOrchestratedAuth(userAuth, entities)
                }
            }
        }

    private fun Refresh.doRefresh() =
        refreshApplicationService.refresh(this)
            .logRight { info("user refresh token in: {}", it) }

    private fun OrchestratedUserRefresh.orchestrateEntities(userAuth: UserAuth) =
        orchestrateEntities.orchestrate(
            user,
            refresh.userType,
            userAuth,
            orchestratedEntityParameters,
        ).logRight { info("orchestrated entities: {}", it) }

    private fun buildOrchestratedAuth(
        userAuth: UserAuth,
        orchestrateEntities: OrchestratedEntities?
    ) =
        toOrchestratedAuthMapper.mapFrom(userAuth, orchestrateEntities)
            .log { info("orchestrated auth mapped: {}", it) }

    companion object : CompanionLogger()
}
