package com.menta.bff.devices.login.entities.installments.out

import com.menta.bff.devices.login.entities.installments.aAcquirersInstallments
import com.menta.bff.devices.login.entities.installments.adapter.out.FindAcquirersInstallmentsClient
import com.menta.bff.devices.login.entities.installments.adapter.out.Page
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.unhandledException
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
import java.util.UUID

class FindAcquirersInstallmentsClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L

    val client = FindAcquirersInstallmentsClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find acquirers installments by merchant id") {
        val id = UUID.randomUUID()
        val userAuth = aUserAuthResponseWithToken()
        val uri = URI("?merchantId=$id")

        scenario("acquirers installments found") {
            val installments = listOf(aAcquirersInstallments())
            val page = Page(installments)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Page::class.java) } returns Mono.just(page)

            client.findBy(id, userAuth) shouldBeRight installments

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

            client.findBy(id, userAuth) shouldBeLeft notFound("acquirers installments for merchant id : $id not found")

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

            client.findBy(id, userAuth) shouldBeLeft unhandledException(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { requestSpec.header("Authorization", "Bearer ${userAuth.token?.idToken}") }
            verify(exactly = 1) { responseSpec.bodyToMono(Page::class.java) }
        }
    }
})
