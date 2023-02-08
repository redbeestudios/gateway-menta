package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedLoginRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserCredentials
import com.menta.bff.devices.login.shared.adapter.out.model.OrchestratedEntitiesRequest
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.domain.UserCredentials
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToOrchestratedUserCredentialsMapperSpec : FeatureSpec({

    val toOrchestratedEntityParametersMapper = mockk<ToOrchestratedEntityParametersMapper>()
    val mapper = ToOrchestratedUserCredentialsMapper(toOrchestratedEntityParametersMapper)

    beforeEach { clearAllMocks() }

    feature("mapper OrchestratedLoginRequest to OrchestratedUserCredentials") {
        scenario("from userAuth") {
            val request = OrchestratedLoginRequest(
                user = "user",
                password = "password",
                userType = MERCHANT,
                orchestratedEntities = null
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserCredentials(
                userCredentials = UserCredentials(
                    user = "user",
                    password = "password",
                    userType = MERCHANT
                ),
                orchestratedEntityParameters = null
            )
        }
        scenario("from userAuth and customer") {
            val orchestratedEntitiesRequest = OrchestratedEntitiesRequest(
                terminalSerialCode = "567",
                terminalModel = "I2000"
            )
            val request = OrchestratedLoginRequest(
                user = "user",
                password = "password",
                userType = MERCHANT,
                orchestratedEntities = orchestratedEntitiesRequest
            )

            every { toOrchestratedEntityParametersMapper.mapFrom(orchestratedEntitiesRequest) } returns OrchestratedEntityParameters(
                terminalSerialCode = "567",
                terminalModel = "I2000"
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserCredentials(
                userCredentials = UserCredentials(
                    user = "user",
                    password = "password",
                    userType = MERCHANT
                ),
                orchestratedEntityParameters = OrchestratedEntityParameters(
                    terminalSerialCode = "567",
                    terminalModel = "I2000"
                )
            )
            verify(exactly = 1) { toOrchestratedEntityParametersMapper.mapFrom(orchestratedEntitiesRequest) }
        }
    }
})
