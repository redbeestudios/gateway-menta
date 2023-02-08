package com.menta.bff.devices.login.entities.merchant.adapter.out

import com.menta.bff.devices.login.entities.merchant.aMerchant
import com.menta.bff.devices.login.entities.merchant.domain.Merchant
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

class FindMerchantClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L

    val client = FindMerchantClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find merchant by id") {
        val id = UUID.randomUUID()
        val uri = URI("/".plus(id.toString()))

        scenario("merchant found") {
            val merchant = aMerchant()

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Merchant::class.java) } returns Mono.just(merchant)

            client.findBy(id) shouldBeRight merchant

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Merchant::class.java) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Merchant::class.java) } returns Mono.error(error)

            client.findBy(id) shouldBeLeft unhandledException(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Merchant::class.java) }
        }
        scenario("client returned null") {
            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(Merchant::class.java) } returns Mono.empty()

            client.findBy(id) shouldBeLeft notFound("merchant $id not found")

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(Merchant::class.java) }
        }
    }
})
