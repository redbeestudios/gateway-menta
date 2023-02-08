package com.kiwi.api.payments.adapter.rest

import com.kiwi.api.payments.application.anAcquirerTerminal
import com.kiwi.api.payments.domain.AcquirerTerminal
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID

class AcquirerTerminalRestClientSpec : FeatureSpec({

    feature("Execute web client Terminal") {

        scenario("Successful find terminal") {
            val response = mockk<WebClient.ResponseSpec>()
            val specUri = mockk<WebClient.RequestHeadersUriSpec<*>>()
            val specHeaders = mockk<WebClient.RequestHeadersSpec<*>>()
            val client = mockk<WebClient>()
            val resAcquirer = anAcquirerTerminal()
            val url = "http://localhost:8080/private/v1/terminals/{terminalId}/acquirers/{acquirerId}"

            every { response.bodyToMono(AcquirerTerminal::class.java) } returns Mono.just(resAcquirer)
            every { specUri.uri(url, UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"), "GPS") } returns specHeaders
            every { specHeaders.retrieve() } returns response
            every { client.get() } returns specUri

            AcquirerTerminalRestClient(
                url = url,
                timeout = 10000, client
            )
                .findBy(
                    UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")
                )
                .shouldBe(resAcquirer)
        }
    }
})
