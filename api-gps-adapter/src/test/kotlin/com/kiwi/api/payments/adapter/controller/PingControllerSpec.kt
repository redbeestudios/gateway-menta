package com.kiwi.api.payments.adapter.controller

import arrow.core.right
import com.kiwi.api.payments.application.port.`in`.PingInPort
import com.kiwi.api.payments.shared.error.providers.ErrorResponseProvider
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import javax.servlet.http.HttpServletRequest

class PingControllerSpec : FeatureSpec({

    val httpServletRequest = mockk<HttpServletRequest>()
    val pingInPort = mockk<PingInPort>()
    val errorResponseProvider: ErrorResponseProvider = mockk()
    val pingController = PingController(
        pingInPort,
        errorResponseProvider
    )

    beforeEach {
        clearAllMocks()
        every { httpServletRequest.requestURI } returns "/private/ping"
        every { httpServletRequest.queryString } returns null
    }

    val mockMvc =
        MockMvcBuilders
            .standaloneSetup(pingController)
            .setControllerAdvice(aControllerAdvicePayment(httpServletRequest))
            .build()

    feature("Ping Controller") {
        scenario("Successful Ping") {

            every { pingInPort.execute() } returns Unit.right()

            mockMvc.perform(
                MockMvcRequestBuilders.post(
                    "/private/ping"
                )
            ).andExpect(status().isNoContent)
        }
    }
})
