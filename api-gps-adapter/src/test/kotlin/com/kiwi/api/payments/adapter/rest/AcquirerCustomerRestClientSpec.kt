package com.kiwi.api.payments.adapter.rest

import com.kiwi.api.payments.application.anAcquirerCustomer
import com.kiwi.api.payments.domain.AcquirerCustomer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID

class AcquirerCustomerRestClientSpec : FeatureSpec({

    feature("Execute web client customer") {

        scenario("Successful find customer") {
            val response = mockk<WebClient.ResponseSpec>()
            val specUri = mockk<WebClient.RequestHeadersUriSpec<*>>()
            val specHeaders = mockk<WebClient.RequestHeadersSpec<*>>()
            val client = mockk<WebClient>()
            val respAcquirer = anAcquirerCustomer()
            val url = "http://localhost:8080/private/v1/customers/{customerId}/acquirers/{acquirerId}"

            every { specUri.uri(url, UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"), "GPS") } returns specHeaders
            every { client.get() } returns specUri
            every { specHeaders.retrieve() } returns response
            every { response.bodyToMono(AcquirerCustomer::class.java) } returns Mono.just(respAcquirer)

            AcquirerCustomerRestClient(
                url = url,
                timeout = 10000, client
            )
                .findBy(
                    UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")
                )
                .shouldBe(respAcquirer)
        }

        scenario("Unsuccessful find customer") {
            val response = mockk<WebClient.ResponseSpec>()
            val specUri = mockk<WebClient.RequestHeadersUriSpec<*>>()
            val specHeaders = mockk<WebClient.RequestHeadersSpec<*>>()
            val client = mockk<WebClient>()
            val respAcquirer = anAcquirerCustomer()
            val url = "http://localhost:8080/private/v1/customers/{customerId}/acquirers/{acquirerId}"

            every { specUri.uri(url, UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96"), "GPS") } returns specHeaders
            every { client.get() } returns specUri
            every { specHeaders.retrieve() } throws Exception()

            AcquirerCustomerRestClient(
                url = url,
                timeout = 10000, client
            )
                .findBy(
                    UUID.fromString("5B1FC6C0-9BBA-4DFA-B128-786E52F6AA96")
                )
                .shouldBeNull()
        }
    }
})
