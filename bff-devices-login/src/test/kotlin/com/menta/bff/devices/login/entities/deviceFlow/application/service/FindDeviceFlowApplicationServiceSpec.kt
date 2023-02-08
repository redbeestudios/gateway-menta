package com.menta.bff.devices.login.entities.deviceFlow.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.deviceFlow.anDevicesFlows
import com.menta.bff.devices.login.entities.deviceFlow.application.port.out.FindDeviceFlowPortOut
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.reactive.function.client.WebClientResponseException

class FindDeviceFlowApplicationServiceSpec : FeatureSpec({
    val findDeviceFlowsPortOut = mockk<FindDeviceFlowPortOut>()
    val service = FindDeviceFlowApplicationService(findDeviceFlowsPortOut)

    beforeEach { clearAllMocks() }

    feature("find device flows by terminalModel") {
        val terminalModel = "I2000"
        val userAuth = aUserAuthResponseWithToken()

        scenario("device flows found") {
            val deviceFlows = anDevicesFlows()

            every { findDeviceFlowsPortOut.findBy(terminalModel, userAuth) } returns deviceFlows.right()

            service.findBy(terminalModel, userAuth) shouldBeRight deviceFlows

            verify(exactly = 1) { findDeviceFlowsPortOut.findBy(terminalModel, userAuth) }
        }
        scenario("error searching for device flows") {
            val error = clientError(WebClientResponseException(500, "a status text", null, null, null))

            every { findDeviceFlowsPortOut.findBy(terminalModel, userAuth) } returns error.left()

            service.findBy(terminalModel, userAuth) shouldBeLeft error

            verify(exactly = 1) { findDeviceFlowsPortOut.findBy(terminalModel, userAuth) }
        }
    }
})
