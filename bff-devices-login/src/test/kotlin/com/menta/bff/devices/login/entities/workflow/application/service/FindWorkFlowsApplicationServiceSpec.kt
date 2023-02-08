package com.menta.bff.devices.login.entities.workflow.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.workflow.aWorkFlows
import com.menta.bff.devices.login.entities.workflow.application.port.out.FindWorkFlowsPortOut
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.reactive.function.client.WebClientResponseException

class FindWorkFlowsApplicationServiceSpec : FeatureSpec({
    val findWorkFlowsPortOut = mockk<FindWorkFlowsPortOut>()
    val service = FindWorkFlowsApplicationService(findWorkFlowsPortOut)

    beforeEach { clearAllMocks() }

    feature("find workflows by email and type") {
        val email = "email@menta.global"
        val type = MERCHANT
        val userAuth = aUserAuthResponseWithToken()

        scenario("workflows found") {
            val workflows = aWorkFlows()

            every { findWorkFlowsPortOut.findBy(email, type, userAuth) } returns workflows.right()

            service.find(email, type, userAuth) shouldBeRight workflows

            verify(exactly = 1) { findWorkFlowsPortOut.findBy(email, type, userAuth) }
        }

        scenario("error searching for workflows") {
            val error = ApplicationError.clientError(WebClientResponseException(500, "a status text", null, null, null))

            every { findWorkFlowsPortOut.findBy(email, type, userAuth) } returns error.left()

            service.find(email, type, userAuth) shouldBeLeft error

            verify(exactly = 1) { findWorkFlowsPortOut.findBy(email, type, userAuth) }
        }
    }
})
