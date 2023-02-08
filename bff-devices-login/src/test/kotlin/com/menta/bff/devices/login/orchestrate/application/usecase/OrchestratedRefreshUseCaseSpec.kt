package com.menta.bff.devices.login.orchestrate.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.acquirers.aAcquirers
import com.menta.bff.devices.login.entities.customer.aCustomer
import com.menta.bff.devices.login.entities.deviceFlow.anDevicesFlows
import com.menta.bff.devices.login.entities.merchant.aMerchant
import com.menta.bff.devices.login.entities.terminal.aTerminal
import com.menta.bff.devices.login.entities.workflow.aWorkFlows
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.login.aUserCredentials
import com.menta.bff.devices.login.login.refresh.application.service.RefreshApplicationService
import com.menta.bff.devices.login.orchestrate.application.service.OrchestrateEntitiesService
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRefresh
import com.menta.bff.devices.login.orchestrate.domain.mapper.ToOrchestratedAuthMapper
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrchestratedRefreshUseCaseSpec : FeatureSpec({

    val refreshApplicationService = mockk<RefreshApplicationService>()
    val orchestrateEntitiesService = mockk<OrchestrateEntitiesService>()
    val toOrchestratedAuthMapper = mockk<ToOrchestratedAuthMapper>()

    val client = OrchestratedRefreshUseCase(
        refreshApplicationService = refreshApplicationService,
        orchestrateEntities = orchestrateEntitiesService,
        toOrchestratedAuthMapper = toOrchestratedAuthMapper
    )

    beforeEach { clearAllMocks() }

    feature("orchestrated refresh") {
        val credentials = aUserCredentials()
        val userAuth = aUserAuthResponseWithToken()
        val refreshCredentials = Refresh(token = "token", userType = MERCHANT)
        val customer = aCustomer()
        val merchant = aMerchant()
        val terminal = aTerminal()
        val workFlows = aWorkFlows()
        val acquirers = aAcquirers()
        val deviceFlows = anDevicesFlows()

        scenario("refresh successful, orchestration entities found") {
            val orchestratedUserRefresh = OrchestratedUserRefresh(
                user = "user",
                refresh = refreshCredentials,
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = "I2000"
                )
            )

            val orchestratedAuth = OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = OrchestratedEntities(
                    customer = customer,
                    merchant = merchant,
                    terminal = terminal,
                    workflows = workFlows,
                    acquirers = acquirers,
                    deviceFlows = deviceFlows
                )
            )

            every { refreshApplicationService.refresh(refreshCredentials) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserRefresh.orchestratedEntityParameters
                )
            } returns orchestratedAuth.orchestratedEntities.right()
            every {
                toOrchestratedAuthMapper.mapFrom(
                    userAuth,
                    orchestratedAuth.orchestratedEntities
                )
            } returns orchestratedAuth

            client.refresh(orchestratedUserRefresh) shouldBeRight orchestratedAuth

            verify(exactly = 1) { refreshApplicationService.refresh(refreshCredentials) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserRefresh.orchestratedEntityParameters
                )
            }
            verify(exactly = 1) { toOrchestratedAuthMapper.mapFrom(userAuth, orchestratedAuth.orchestratedEntities) }
        }
        scenario("refresh successful without orchestrated entities") {
            val orchestratedUserCredentials = OrchestratedUserRefresh(
                user = "user",
                refresh = refreshCredentials,
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = "I2000"
                )
            )
            val orchestratedAuth = OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = null
            )

            every { refreshApplicationService.refresh(refreshCredentials) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserCredentials.orchestratedEntityParameters
                )
            } returns null.right()
            every {
                toOrchestratedAuthMapper.mapFrom(
                    userAuth,
                    orchestratedAuth.orchestratedEntities
                )
            } returns orchestratedAuth

            client.refresh(orchestratedUserCredentials) shouldBeRight orchestratedAuth

            verify(exactly = 1) { refreshApplicationService.refresh(refreshCredentials) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserCredentials.orchestratedEntityParameters
                )
            }
            verify(exactly = 1) { toOrchestratedAuthMapper.mapFrom(userAuth, orchestratedAuth.orchestratedEntities) }
        }
        scenario("refresh successful, with entities parameters") {
            val orchestratedUserCredentials = OrchestratedUserRefresh(
                refresh = refreshCredentials,
                user = "user",
                orchestratedEntityParameters = null
            )

            val orchestratedAuth = OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = OrchestratedEntities(
                    customer = customer,
                    merchant = merchant,
                    terminal = null,
                    workflows = workFlows,
                    acquirers = acquirers,
                    deviceFlows = deviceFlows
                )
            )

            every { refreshApplicationService.refresh(refreshCredentials) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserCredentials.orchestratedEntityParameters
                )
            } returns orchestratedAuth.orchestratedEntities.right()
            every {
                toOrchestratedAuthMapper.mapFrom(
                    userAuth,
                    orchestratedAuth.orchestratedEntities
                )
            } returns orchestratedAuth

            client.refresh(orchestratedUserCredentials) shouldBeRight orchestratedAuth

            verify(exactly = 1) { refreshApplicationService.refresh(refreshCredentials) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserCredentials.orchestratedEntityParameters
                )
            }
            verify(exactly = 1) { toOrchestratedAuthMapper.mapFrom(userAuth, orchestratedAuth.orchestratedEntities) }
        }
        scenario("refresh unsuccessful, error doing refresh token") {
            val error = unauthorizedUser()
            val orchestratedUserRefresh = OrchestratedUserRefresh(
                refresh = refreshCredentials,
                user = "user",
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = "I2000"
                )
            )

            every { refreshApplicationService.refresh(refreshCredentials) } returns error.left()

            client.refresh(orchestratedUserRefresh) shouldBeLeft error

            verify(exactly = 1) { refreshApplicationService.refresh(refreshCredentials) }
            verify(exactly = 0) { orchestrateEntitiesService.orchestrate(any(), any(), any(), any()) }
            verify(exactly = 0) { toOrchestratedAuthMapper.mapFrom(any(), any()) }
        }
        scenario("refresh unsuccessful, error orchestrating entities") {
            val orchestratedUserRefresh = OrchestratedUserRefresh(
                refresh = refreshCredentials,
                user = "user",
                orchestratedEntityParameters = null
            )
            val error = notFound("user ${credentials.user} for ${credentials.userType} not found")

            every { refreshApplicationService.refresh(refreshCredentials) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserRefresh.orchestratedEntityParameters
                )
            } returns error.left()

            client.refresh(orchestratedUserRefresh) shouldBeLeft error

            verify(exactly = 1) { refreshApplicationService.refresh(refreshCredentials) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserRefresh.orchestratedEntityParameters
                )
            }
            verify(exactly = 0) { toOrchestratedAuthMapper.mapFrom(any(), any()) }
        }
    }
})
