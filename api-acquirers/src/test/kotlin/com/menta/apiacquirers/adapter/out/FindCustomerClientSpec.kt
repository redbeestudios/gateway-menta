package com.menta.apiacquirers.adapter.out

import com.menta.apiacquirers.aCustomer
import com.menta.apiacquirers.aCustomerResponse
import com.menta.apiacquirers.adapter.out.mapper.ToCustomerMapper
import com.menta.apiacquirers.adapter.out.model.AcquirerCustomerResponse
import com.menta.apiacquirers.adapter.out.model.CustomerResponse
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.clientError
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.customerNotFound
import com.menta.apiacquirers.shared.error.model.ApplicationError.Companion.timeoutError
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

class FindCustomerClientSpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()
    val uriProvider = ""
    val timeout = 500L
    val mapper = mockk<ToCustomerMapper>()

    val client = FindCustomerClient(
        webClient = webClient,
        url = uriProvider,
        timeout = timeout,
        toCustomerMapper = mapper
    )

    beforeEach { clearAllMocks() }

    feature("find customer by id") {
        val id = UUID.randomUUID()
        val uri = URI("/".plus(id.toString()))

        scenario("customer found") {
            val customer = aCustomer()
            val response = aCustomerResponse()

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.just(response)
            every { mapper.mapFrom(response) } returns customer

            client.findBy(id) shouldBeRight customer

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 1) { mapper.mapFrom(response) }
        }
        scenario("client returned 404") {
            val ex = WebClientResponseException(404, "Customer not found for id $id", null, null, null)
            val error = customerNotFound(id)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.error(ex)

            client.findBy(id) shouldBeLeft error

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }
        scenario("client returned 408") {
            val ex = WebClientResponseException(408, "timeout", null, null, null)
            val error = timeoutError()

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.error(ex)

            client.findBy(id) shouldBeLeft error

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }
        scenario("client returned generic error") {
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.error(error)

            client.findBy(id) shouldBeLeft clientError(error)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }
        scenario("client returned null") {
            every { webClient.get() } returns requestSpec
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.empty()

            client.findBy(id) shouldBeLeft customerNotFound(id)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 0) { mapper.mapFrom(any()) }
        }
    }
})
