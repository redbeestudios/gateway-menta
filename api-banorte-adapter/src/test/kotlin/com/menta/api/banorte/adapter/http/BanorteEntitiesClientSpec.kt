package com.menta.api.banorte.adapter.http

import com.menta.api.banorte.application.aMerchant
import com.menta.api.banorte.application.merchantId
import com.menta.api.banorte.domain.BanorteMerchant
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

class BanorteEntitiesClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L

    val client = BanorteEntitiesClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find banorte merchant by merchant id") {
        val id = merchantId
        val uri = URI("/$merchantId")
        val merchant = aMerchant()

        scenario("merchant found") {
            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(BanorteMerchant::class.java) } returns Mono.just(merchant)

            client.findBy(id) shouldBeRight merchant

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(BanorteMerchant::class.java) }
        }
    }
})
