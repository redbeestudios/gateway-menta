package com.menta.api.feenicia.adapter.controller.rest

import com.menta.api.feenicia.adapter.rest.FeeniciaEntitiesRestClient
import com.menta.api.feenicia.application.aFeeniciaMerchant
import com.menta.api.feenicia.domain.FeeniciaMerchant
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID

class FeeniciaEntitiesRestClientSpec : FeatureSpec({

    feature("Execute web client feenicia merchant") {

        scenario("Successful find feenicia merchant") {
            val response = mockk<WebClient.ResponseSpec>()
            val specUri = mockk<WebClient.RequestHeadersUriSpec<*>>()
            val specHeaders = mockk<WebClient.RequestHeadersSpec<*>>()
            val client = mockk<WebClient>()
            val respFeeniciaMerchant = aFeeniciaMerchant()
            val url = "http://localhost:8080/private/feenicia-merchants/{merchantId}"

            every { response.bodyToMono(FeeniciaMerchant::class.java) } returns Mono.just(respFeeniciaMerchant)
            every { specUri.uri(url, UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")) } returns specHeaders
            every { specHeaders.retrieve() } returns response
            every { client.get() } returns specUri

            FeeniciaEntitiesRestClient(
                url = url,
                timeout = 10000, client
            )
                .findBy(
                    UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")
                )
                .shouldBe(respFeeniciaMerchant)
        }
    }
})
