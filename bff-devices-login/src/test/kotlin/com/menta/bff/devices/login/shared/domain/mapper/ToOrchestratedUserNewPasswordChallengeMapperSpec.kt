package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedNewPasswordChallengeRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserChallenge
import com.menta.bff.devices.login.shared.adapter.out.model.OrchestratedEntitiesRequest
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToOrchestratedUserNewPasswordChallengeMapperSpec : FeatureSpec({

    val toOrchestratedEntityParametersMapper = mockk<ToOrchestratedEntityParametersMapper>()
    val mapper = ToOrchestratedUserNewPasswordChallengeMapper(toOrchestratedEntityParametersMapper)

    beforeEach { clearAllMocks() }

    feature("mapper OrchestratedNewPasswordChallengeRequest to OrchestratedUserChallenge") {
        scenario("from userAuth") {
            val request = OrchestratedNewPasswordChallengeRequest(
                session = "a session",
                user = "user@user.com",
                userType = MERCHANT,
                newPassword = "a new password",
                orchestratedEntities = null
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = "a session",
                    user = "user@user.com",
                    userType = MERCHANT,
                    newPassword = "a new password",
                ),
                orchestratedEntityParameters = null
            )
        }
        scenario("with terminalSerialCode") {
            val orchestratedEntitiesRequest = OrchestratedEntitiesRequest(
                terminalSerialCode = "567",
                terminalModel = null
            )
            val request = OrchestratedNewPasswordChallengeRequest(
                session = "a session",
                user = "user@user.com",
                userType = MERCHANT,
                newPassword = "a new password",
                orchestratedEntities = orchestratedEntitiesRequest
            )

            every { toOrchestratedEntityParametersMapper.mapFrom(orchestratedEntitiesRequest) } returns OrchestratedEntityParameters(
                terminalSerialCode = "567",
                terminalModel = null
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = "a session",
                    user = "user@user.com",
                    userType = MERCHANT,
                    newPassword = "a new password",
                ),
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = null
                )
            )
            verify(exactly = 1) { toOrchestratedEntityParametersMapper.mapFrom(orchestratedEntitiesRequest) }
        }
        scenario("with terminalModel") {
            val orchestratedEntitiesRequest = OrchestratedEntitiesRequest(
                terminalSerialCode = null,
                terminalModel = "I2000"
            )
            val request = OrchestratedNewPasswordChallengeRequest(
                session = "a session",
                user = "user@user.com",
                userType = MERCHANT,
                newPassword = "a new password",
                orchestratedEntities = orchestratedEntitiesRequest
            )

            every { toOrchestratedEntityParametersMapper.mapFrom(orchestratedEntitiesRequest) } returns OrchestratedEntityParameters(
                terminalSerialCode = null,
                terminalModel = "I2000"
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserChallenge(
                newPasswordChallengeSolution = NewPasswordChallengeSolution(
                    session = "a session",
                    user = "user@user.com",
                    userType = MERCHANT,
                    newPassword = "a new password",
                ),
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = null,
                    terminalModel = "I2000"
                )
            )
            verify(exactly = 1) { toOrchestratedEntityParametersMapper.mapFrom(orchestratedEntitiesRequest) }
        }
    }
})
