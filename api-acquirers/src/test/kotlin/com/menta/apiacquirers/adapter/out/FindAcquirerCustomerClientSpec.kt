package com.menta.apiacquirers.adapter.out

import com.menta.apiacquirers.aAcquirerCustomer
import com.menta.apiacquirers.aAcquirerCustomerResponse
import com.menta.apiacquirers.adapter.out.mapper.ToAcquirerCustomerMapper
import com.menta.apiacquirers.adapter.out.model.AcquirerCustomerResponse
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.acquirerCustomerNotFound
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.clientError
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

class FindAcquirerCustomerClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L
    val mapper = mockk<ToAcquirerCustomerMapper>()

    val client = FindAcquirerCustomerClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout,
        toAcquirerCustomer = mapper
    )

    beforeEach { clearAllMocks() }

    feature("find acquirer customer by id and acquirer") {
        val id = UUID.randomUUID()
        val acquirer = "GPS"
        val uri = URI("/$id/acquirers/$acquirer")

        scenario("acquirer customer found") {
            val acquirerCustomer = aAcquirerCustomer()
            val response = aAcquirerCustomerResponse()

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(AcquirerCustomerResponse::class.java) } returns Mono.just(response)
            every { mapper.mapFrom(response) } returns acquirerCustomer

            client.findBy(id, acquirer) shouldBeRight acquirerCustomer

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(AcquirerCustomerResponse::class.java) }
            verify(exactly = 1) { mapper.mapFrom(response) }
        }
        scenario("client returned 404") {
            val error = WebClientResponseException(404, "acquirer customer $id not found", null, null, null)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(AcquirerCustomerResponse::class.java) } returns Mono.error(error)

            client.findBy(id, acquirer) shouldBeLeft clientError(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(AcquirerCustomerResponse::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(AcquirerCustomerResponse::class.java) } returns Mono.error(error)

            client.findBy(id, acquirer) shouldBeLeft clientError(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(AcquirerCustomerResponse::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }
        scenario("client returned null") {
            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(AcquirerCustomerResponse::class.java) } returns Mono.empty()

            client.findBy(id, acquirer) shouldBeLeft acquirerCustomerNotFound(id, acquirer)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(AcquirerCustomerResponse::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }
    }
})
