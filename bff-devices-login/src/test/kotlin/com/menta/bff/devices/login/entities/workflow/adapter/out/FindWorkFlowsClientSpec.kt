package com.menta.bff.devices.login.entities.workflow.adapter.out

import com.menta.bff.devices.login.entities.workflow.aWorkFlows
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.shared.domain.UserType.MERCHANT
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

class FindWorkFlowsClientSpec : FeatureSpec({
    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val url = "an.url/workflows"
    val timeout = 500L

    val client = FindWorkFlowsClient(
        webClient = webClient,
        url = url,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find workflows by email and type") {
        val email = "user@menta.global"
        val type = MERCHANT
        val userAuth = aUserAuthResponseWithToken()

        val uri = URI("$url?email=$email&type=$type")

        scenario("workflows found") {
            val workFlows = aWorkFlows()
            val pageWorkFlows = Page(workFlows)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.just(pageWorkFlows)

            client.findBy(email, type, userAuth) shouldBeRight workFlows

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

            client.findBy(email, type, userAuth) shouldBeLeft clientError(error)

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

            client.findBy(email, type, userAuth) shouldBeLeft notFound("workflows for user $email for $type not found")

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
    }
})
