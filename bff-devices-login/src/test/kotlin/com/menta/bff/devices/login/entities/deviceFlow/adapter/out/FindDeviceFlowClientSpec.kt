package com.menta.bff.devices.login.entities.deviceFlow.adapter.out

import com.menta.bff.devices.login.entities.deviceFlow.anDevicesFlows
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI

class FindDeviceFlowClientSpec : FeatureSpec({
    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val url = "an.url/device-flows"
    val timeout = 500L

    val client = FindDeviceFlowsClient(
        webClient = webClient,
        url = url,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find device flows by terminalModel") {
        val terminalModel = "I2000"
        val userAuth = aUserAuthResponseWithToken()
        val uri = URI("$url?terminalModel=$terminalModel")

        scenario("device flows found") {
            val deviceFlows = anDevicesFlows()
            val pageDeviceFlows = Page(deviceFlows)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.just(pageDeviceFlows)

            client.findBy(terminalModel, userAuth) shouldBeRight deviceFlows

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.error(error)

            client.findBy(terminalModel, userAuth) shouldBeLeft clientError(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
        scenario("client returned null") {
            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.empty()

            client.findBy(terminalModel, userAuth) shouldBeLeft notFound("device flows for terminal model $terminalModel not found")

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
    }
})
