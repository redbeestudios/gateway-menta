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
import com.menta.bff.devices.login.login.challenge.application.service.NewPasswordChallengeApplicationService
import com.menta.bff.devices.login.orchestrate.application.service.OrchestrateEntitiesService
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedAuth
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserChallenge
import com.menta.bff.devices.login.orchestrate.domain.mapper.ToOrchestratedAuthMapper
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.OrchestratedEntities
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
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

class OrchestratedNewPasswordChallengeUseCaseSpec : FeatureSpec({

    val newPasswordChallengeApplicationService = mockk<NewPasswordChallengeApplicationService>()
    val orchestrateEntitiesService = mockk<OrchestrateEntitiesService>()
    val toOrchestratedAuthMapper = mockk<ToOrchestratedAuthMapper>()

    val client = OrchestratedNewPasswordChallengeUseCase(
        newPasswordChallengeApplicationService = newPasswordChallengeApplicationService,
        orchestrateEntities = orchestrateEntitiesService,
        toOrchestratedAuthMapper = toOrchestratedAuthMapper
    )

    beforeEach { clearAllMocks() }

    feature("orchestrated new password challenge") {

        val userAuth = aUserAuthResponseWithToken()
        val customer = aCustomer()
        val merchant = aMerchant()
        val terminal = aTerminal()
        val workFlows = aWorkFlows()
        val acquirers = aAcquirers()
        val deviceFlows = anDevicesFlows()

        scenario("new password challenge successful, orchestration entities found") {
            val orchestratedUserChallenge = OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = "a session",
                    user = "user@user.com",
                    userType = MERCHANT,
                    newPassword = "a new password",
                ),
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

            every { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    orchestratedUserChallenge.newPasswordChallengeSolution.user,
                    orchestratedUserChallenge.newPasswordChallengeSolution.userType,
                    userAuth,
                    orchestratedUserChallenge.orchestratedEntityParameters
                )
            } returns orchestratedAuth.orchestratedEntities.right()
            every {
                toOrchestratedAuthMapper.mapFrom(
                    userAuth,
                    orchestratedAuth.orchestratedEntities
                )
            } returns orchestratedAuth

            client.solve(orchestratedUserChallenge) shouldBeRight orchestratedAuth

            verify(exactly = 1) { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    orchestratedUserChallenge.newPasswordChallengeSolution.user,
                    orchestratedUserChallenge.newPasswordChallengeSolution.userType,
                    userAuth,
                    orchestratedUserChallenge.orchestratedEntityParameters
                )
            }
            verify(exactly = 1) { toOrchestratedAuthMapper.mapFrom(userAuth, orchestratedAuth.orchestratedEntities) }
        }

        scenario("new password challenge without orchestrated entities") {
            val orchestratedUserChallenge = OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = "a session",
                    user = "user@user.com",
                    userType = MERCHANT,
                    newPassword = "a new password",
                ),
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = "I2000"
                )
            )
            val orchestratedAuth = OrchestratedAuth(
                userAuth = userAuth,
                orchestratedEntities = null
            )

            every { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    orchestratedUserChallenge.newPasswordChallengeSolution.user,
                    orchestratedUserChallenge.newPasswordChallengeSolution.userType,
                    userAuth,
                    orchestratedUserChallenge.orchestratedEntityParameters
                )
            } returns null.right()
            every {
                toOrchestratedAuthMapper.mapFrom(
                    userAuth,
                    orchestratedAuth.orchestratedEntities
                )
            } returns orchestratedAuth

            client.solve(orchestratedUserChallenge) shouldBeRight orchestratedAuth

            verify(exactly = 1) { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    orchestratedUserChallenge.newPasswordChallengeSolution.user,
                    orchestratedUserChallenge.newPasswordChallengeSolution.userType,
                    userAuth,
                    orchestratedUserChallenge.orchestratedEntityParameters
                )
            }
            verify(exactly = 1) { toOrchestratedAuthMapper.mapFrom(userAuth, orchestratedAuth.orchestratedEntities) }
        }
        scenario("new password challenge successful, with entities parameters") {
            val orchestratedUserChallenge = OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = "a session",
                    user = "user@user.com",
                    userType = MERCHANT,
                    newPassword = "a new password",
                ),
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

            every { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    orchestratedUserChallenge.newPasswordChallengeSolution.user,
                    orchestratedUserChallenge.newPasswordChallengeSolution.userType,
                    userAuth,
                    orchestratedUserChallenge.orchestratedEntityParameters
                )
            } returns orchestratedAuth.orchestratedEntities.right()
            every {
                toOrchestratedAuthMapper.mapFrom(
                    userAuth,
                    orchestratedAuth.orchestratedEntities
                )
            } returns orchestratedAuth

            client.solve(orchestratedUserChallenge) shouldBeRight orchestratedAuth

            verify(exactly = 1) { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    orchestratedUserChallenge.newPasswordChallengeSolution.user,
                    orchestratedUserChallenge.newPasswordChallengeSolution.userType,
                    userAuth,
                    orchestratedUserChallenge.orchestratedEntityParameters
                )
            }
            verify(exactly = 1) { toOrchestratedAuthMapper.mapFrom(userAuth, orchestratedAuth.orchestratedEntities) }
        }
        scenario("new password challenge unsuccessful, error doing challenge") {
            val error = unauthorizedUser()
            val orchestratedUserChallenge = OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = "a session",
                    user = "user@user.com",
                    userType = MERCHANT,
                    newPassword = "a new password",
                ),
                orchestratedEntityParameters = null
            )

            every { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) } returns error.left()

            client.solve(orchestratedUserChallenge) shouldBeLeft error

            verify(exactly = 1) { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) }
            verify(exactly = 0) { orchestrateEntitiesService.orchestrate(any(), any(), any(), any()) }
            verify(exactly = 0) { toOrchestratedAuthMapper.mapFrom(any(), any()) }
        }
        scenario("new password challenge unsuccessful, error orchestrating entities") {
            val orchestratedUserChallenge = OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = "a session",
                    user = "user@user.com",
                    userType = MERCHANT,
                    newPassword = "a new password",
                ),
                orchestratedEntityParameters = null
            )
            val error = notFound(
                "user ${orchestratedUserChallenge.newPasswordChallengeSolution.user} for ${orchestratedUserChallenge.newPasswordChallengeSolution.userType} not found"
            )

            every { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) } returns userAuth.right()
            every {
                orchestrateEntitiesService.orchestrate(
                    orchestratedUserChallenge.newPasswordChallengeSolution.user,
                    orchestratedUserChallenge.newPasswordChallengeSolution.userType,
                    userAuth,
                    orchestratedUserChallenge.orchestratedEntityParameters
                )
            } returns error.left()

            client.solve(orchestratedUserChallenge) shouldBeLeft error

            verify(exactly = 1) { newPasswordChallengeApplicationService.solve(orchestratedUserChallenge.newPasswordChallengeSolution) }
            verify(exactly = 1) {
                orchestrateEntitiesService.orchestrate(
                    orchestratedUserChallenge.newPasswordChallengeSolution.user,
                    orchestratedUserChallenge.newPasswordChallengeSolution.userType,
                    userAuth,
                    orchestratedUserChallenge.orchestratedEntityParameters
                )
            }
            verify(exactly = 0) { toOrchestratedAuthMapper.mapFrom(any(), any()) }
        }
    }
})
