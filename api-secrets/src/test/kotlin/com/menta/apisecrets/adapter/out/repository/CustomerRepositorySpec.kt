package com.menta.apisecrets.adapter.out.repository

import arrow.core.right
import com.menta.apisecrets.aCustomer
import com.menta.apisecrets.aCustomerResponse
import com.menta.apisecrets.adapter.out.CustomerRepository
import com.menta.apisecrets.adapter.out.UriProvider
import com.menta.apisecrets.adapter.out.model.CustomerResponse
import com.menta.apisecrets.adapter.out.model.mapper.ToCustomerMapper
import com.menta.apisecrets.shared.error.model.ApplicationError
import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.customerNotFound
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

class CustomerRepositorySpec : FeatureSpec({

    val webClient = mockk<WebClient>()
    val uriProvider = mockk<UriProvider>()
    val toCustomerMapper = mockk<ToCustomerMapper>()
    val timeout = 500L

    val requestSpec = mockk<WebClient.RequestBodyUriSpec>()
    val responseSpec = mockk<WebClient.ResponseSpec>()

    val repository = CustomerRepository(
        webClient = webClient,
        uriProvider = uriProvider,
        toCustomerMapper = toCustomerMapper,
        timeout = timeout
    )

    beforeEach { clearAllMocks() }

    feature("find customer by id") {

        scenario("customer found") {
            val id = UUID.randomUUID()
            val customerResponse = aCustomerResponse()
            val customer = aCustomer()
            val uri = URI("")

            every { webClient.get() } returns requestSpec
            every { uriProvider.provideForCustomer(id.toString()) } returns uri
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.just(customerResponse)
            every { toCustomerMapper.from(customerResponse) } returns customer.right()

            repository.findBy(id) shouldBeRight customer

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForCustomer(id.toString()) }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 1) { toCustomerMapper.from(customerResponse) }
        }

        scenario("client returned 404") {
            val id = UUID.randomUUID()
            val uri = URI("")
            val error = WebClientResponseException(404, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { uriProvider.provideForCustomer(id.toString()) } returns uri
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.error(error)

            repository.findBy(id) shouldBeLeft customerNotFound(id)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForCustomer(id.toString()) }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 0) { toCustomerMapper.from(any()) }
        }

        scenario("client returned timeout") {
            val id = UUID.randomUUID()
            val uri = URI("")
            val error = WebClientResponseException(408, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { uriProvider.provideForCustomer(id.toString()) } returns uri
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.error(error)

            repository.findBy(id) shouldBeLeft ApplicationError.timeout()

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForCustomer(id.toString()) }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 0) { toCustomerMapper.from(any()) }
        }

        scenario("client returned generic error") {
            val id = UUID.randomUUID()
            val uri = URI("")
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { uriProvider.provideForCustomer(id.toString()) } returns uri
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.error(error)

            repository.findBy(id) shouldBeLeft ApplicationError.terminalRepositoryError()

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForCustomer(id.toString()) }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 0) { toCustomerMapper.from(any()) }
        }

        scenario("client returned null") {
            val id = UUID.randomUUID()
            val uri = URI("")
            val error = WebClientResponseException(500, "a status text", null, null, null)

            every { webClient.get() } returns requestSpec
            every { uriProvider.provideForCustomer(id.toString()) } returns uri
            every { requestSpec.uri(uri) } returns requestSpec
            every { requestSpec.retrieve() } returns responseSpec
            every { responseSpec.bodyToMono(CustomerResponse::class.java) } returns Mono.empty()

            repository.findBy(id) shouldBeLeft customerNotFound(id)

            verify(exactly = 1) { webClient.get() }
            verify(exactly = 1) { uriProvider.provideForCustomer(id.toString()) }
            verify(exactly = 1) { requestSpec.uri(uri) }
            verify(exactly = 1) { requestSpec.retrieve() }
            verify(exactly = 1) { responseSpec.bodyToMono(CustomerResponse::class.java) }
            verify(exactly = 0) { toCustomerMapper.from(any()) }
        }
    }
})
