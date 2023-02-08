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
import com.menta.bff.devices.login.login.auth.application.service.LoginApplicationService
import com.menta.bff.devices.login.orchestrate.application.service.OrchestrateEntitiesService
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserCredentials
import com.menta.bff.devices.login.orchestrate.domain.mapper.ToOrchestratedAuthMapper
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrchestratedLoginUseCaseSpec : FeatureSpec({

    val loginApplicationService = mockk<LoginApplicationService>()
    val orchestrateEntitiesService = mockk<OrchestrateEntitiesService>()
    val toOrchestratedAuthMapper = mockk<ToOrchestratedAuthMapper>()

    val client = OrchestratedLoginUseCase(
        loginApplicationService = loginApplicationService,
        orchestrateEntities = orchestrateEntitiesService,
        toOrchestratedAuthMapper = toOrchestratedAuthMapper
    )

    beforeEach { clearAllMocks() }

    feature("orchestrated login") {

        scenario("login successful, orchestration entities found") {

            val credentials = aUserCredentials()
            val userAuth = aUserAuthResponseWithToken()
            val orchestratedUserCredentials = OrchestratedUserCredentials(
                userCredentials = credentials,
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = "I2000"
                )
            )
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val workFlows = aWorkFlows()
            val acquirers = aAcquirers()
            val deviceFlows = anDevicesFlows()

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

            every { loginApplicationService.login(credentials) } returns userAuth.right()
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

            client.login(orchestratedUserCredentials) shouldBeRight orchestratedAuth

            verify(exactly = 1) { loginApplicationService.login(credentials) }
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

        scenario("login successful without orchestrated entities") {

            val credentials = aUserCredentials()
            val userAuth = aUserAuthResponseWithToken()
            val orchestratedUserCredentials = OrchestratedUserCredentials(
                userCredentials = credentials,
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = "I2000"
                )
            )
            val orchestratedAuth = OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = null
            )

            every { loginApplicationService.login(credentials) } returns userAuth.right()
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

            client.login(orchestratedUserCredentials) shouldBeRight orchestratedAuth

            verify(exactly = 1) { loginApplicationService.login(credentials) }
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

        scenario("login successful, without entities parameters") {

            val credentials = aUserCredentials()
            val userAuth = aUserAuthResponseWithToken()
            val orchestratedUserCredentials = OrchestratedUserCredentials(
                userCredentials = credentials,
                orchestratedEntityParameters = null
            )
            val customer = aCustomer()
            val merchant = aMerchant()
            val workFlows = aWorkFlows()
            val acquirers = aAcquirers()
            val deviceFlows = anDevicesFlows()

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

            every { loginApplicationService.login(credentials) } returns userAuth.right()
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

            client.login(orchestratedUserCredentials) shouldBeRight orchestratedAuth

            verify(exactly = 1) { loginApplicationService.login(credentials) }
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

        scenario("login unsuccessful, error doing login") {

            val credentials = aUserCredentials()
            val orchestratedUserCredentials = OrchestratedUserCredentials(
                userCredentials = credentials,
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = "I2000"
                )
            )
            val error = unauthorizedUser()

            every { loginApplicationService.login(credentials) } returns error.left()

            client.login(orchestratedUserCredentials) shouldBeLeft error

            verify(exactly = 1) { loginApplicationService.login(credentials) }
            verify(exactly = 0) { orchestrateEntitiesService.orchestrate(any(), any(), any(), any()) }
            verify(exactly = 0) { toOrchestratedAuthMapper.mapFrom(any(), any()) }
        }

        scenario("login unsuccessful, error orchestrating entities") {

            val credentials = aUserCredentials()
            val userAuth = aUserAuthResponseWithToken()
            val orchestratedUserCredentials = OrchestratedUserCredentials(
                userCredentials = credentials,
                orchestratedEntityParameters = null
            )

            val error = notFound("user ${credentials.user} for ${credentials.userType} not found")

            every { loginApplicationService.login(credentials) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserCredentials.orchestratedEntityParameters
                )
            } returns error.left()

            client.login(orchestratedUserCredentials) shouldBeLeft error

            verify(exactly = 1) { loginApplicationService.login(credentials) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    credentials.user,
                    credentials.userType,
                    userAuth,
                    orchestratedUserCredentials.orchestratedEntityParameters
                )
            }
            verify(exactly = 0) { toOrchestratedAuthMapper.mapFrom(any(), any()) }
        }
    }
})
