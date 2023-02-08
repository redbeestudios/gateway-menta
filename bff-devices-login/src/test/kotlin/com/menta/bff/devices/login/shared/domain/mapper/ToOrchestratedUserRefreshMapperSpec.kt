package com.menta.bff.devices.login.shared.domain.mapper

import com.menta.bff.devices.login.orchestrate.adapter.`in`.model.OrchestratedRefreshRequest
import com.menta.bff.devices.login.orchestrate.domain.OrchestratedUserRefresh
import com.menta.bff.devices.login.shared.adapter.out.model.OrchestratedEntitiesRequest
import com.menta.bff.devices.login.shared.domain.OrchestratedEntityParameters
import com.menta.bff.devices.login.shared.domain.Refresh
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToOrchestratedUserRefreshMapperSpec : FeatureSpec({

    val toOrchestratedEntityParametersMapper = mockk<ToOrchestratedEntityParametersMapper>()
    val mapper = ToOrchestratedUserRefreshMapper(toOrchestratedEntityParametersMapper)

    beforeEach { clearAllMocks() }

    feature("mapper OrchestratedRefreshRequest to OrchestratedUserRefresh") {
        scenario("from userAuth") {
            val request = OrchestratedRefreshRequest(
                refreshToken = "a refresh token",
                user = "user@user.com",
                userType = MERCHANT,
                orchestratedEntities = null
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserRefresh(
                user = "user@user.com",
                refresh = Refresh(
                    token = "a refresh token",
                    userType = MERCHANT
                ),
                orchestratedEntityParameters = null
            )
        }
        scenario("with terminalSerialCode") {
            val orchestratedEntitiesRequest = OrchestratedEntitiesRequest(
                terminalSerialCode = "567",
                terminalModel = null
            )
            val request = OrchestratedRefreshRequest(
                refreshToken = "a refresh token",
                user = "user@user.com",
                userType = MERCHANT,
                orchestratedEntities = orchestratedEntitiesRequest
            )

            every { toOrchestratedEntityParametersMapper.mapFrom(orchestratedEntitiesRequest) } returns OrchestratedEntityParameters(
                terminalSerialCode = "567",
                terminalModel = null
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserRefresh(
                user = "user@user.com",
                refresh = Refresh(
                    token = "a refresh token",
                    userType = MERCHANT
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
            val request = OrchestratedRefreshRequest(
                refreshToken = "a refresh token",
                user = "user@user.com",
                userType = MERCHANT,
                orchestratedEntities = orchestratedEntitiesRequest
            )

            every { toOrchestratedEntityParametersMapper.mapFrom(orchestratedEntitiesRequest) } returns OrchestratedEntityParameters(
                terminalSerialCode = null,
                terminalModel = "I2000"
            )

            mapper.mapFrom(request) shouldBe OrchestratedUserRefresh(
                user = "user@user.com",
                refresh = Refresh(
                    token = "a refresh token",
                    userType = MERCHANT
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
